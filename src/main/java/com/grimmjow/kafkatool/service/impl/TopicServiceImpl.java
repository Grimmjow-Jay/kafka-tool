package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.config.ConstantConfig;
import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.KafkaTopicPartition;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;
import com.grimmjow.kafkatool.domain.request.FetchMessageRequest;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.mapper.ClusterMapper;
import com.grimmjow.kafkatool.service.TopicService;
import com.grimmjow.kafkatool.util.Pair;
import com.grimmjow.kafkatool.vo.KafkaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final KafkaClientPool kafkaClientPool;

    private final ClusterMapper clusterMapper;

    @Value("${topic.data.maxFetchSize:1000}")
    private long maxFetchSize;

    public TopicServiceImpl(KafkaClientPool kafkaClientPool, ClusterMapper clusterMapper) {
        this.kafkaClientPool = kafkaClientPool;
        this.clusterMapper = clusterMapper;
    }

    @Override
    public Set<String> topics(String clusterName) {
        try {
            return kafkaClientPool.getClient(clusterName).topics();
        } catch (KafkaClientException e) {
            log.error("获取集群Topic列表异常", e);
            throw new BaseException("获取集群Topic列表异常");
        }
    }

    @Override
    public KafkaTopic detail(String clusterName, String topic) {
        try {
            return kafkaClientPool.getClient(clusterName).topicDetail(topic);
        } catch (KafkaClientException e) {
            log.error("获取集群Topic详情异常", e);
            throw new BaseException("获取集群Topic详情异常");
        }
    }

    @Override
    public void createTopic(CreateTopicRequest request) {
        try {
            kafkaClientPool.getClient(request.getClusterName())
                    .createTopic(request.getTopic(), request.getPartition(), (short) request.getReplication());
        } catch (KafkaClientException e) {
            log.error("创建Topic异常", e);
            throw new BaseException("创建Topic异常");
        }
    }

    @Override
    public List<KafkaData<String, String>> fetchMessage(String clusterName, String topic, FetchMessageRequest fetchRequest) {
        checkFetchMessageRequest(fetchRequest);

        Cluster cluster = clusterMapper.getByName(clusterName);
        BaseException.assertNull(cluster, "集群不存在");

        KafkaTopic detail = detail(clusterName, topic);
        Collection<KafkaTopicPartition> assignPartitions = detail.getPartitions();
        BaseException.assertEmpty(assignPartitions, "该Topic没有分区信息");

        Integer requestPartition = fetchRequest.getPartition();
        if (requestPartition != null) {
            assignPartitions = assignPartitions.stream()
                    .filter(e -> e.getPartition() == requestPartition)
                    .collect(Collectors.toList());
        }
        BaseException.assertEmpty(assignPartitions, "分区数据有误");

        List<KafkaData<String, String>> result = Lists.newArrayList();
        for (KafkaTopicPartition assignPartition : assignPartitions) {
            Pair<Long, Long> offsetPair = offsetPair(assignPartition.getOffset(), fetchRequest.getStartOffset(), fetchRequest.getEndOffset());
            TopicPartition topicPartition = new TopicPartition(topic, assignPartition.getPartition());
            result.addAll(fetchMessage(cluster.getBootstrapServers(), topicPartition, offsetPair.getKey(), offsetPair.getKey()));
        }
        return result;
    }

    private void checkFetchMessageRequest(FetchMessageRequest fetchRequest) {
        Long requestStartOffset = fetchRequest.getStartOffset();
        Long requestEndOffset = fetchRequest.getEndOffset();

        if (requestStartOffset != null) {
            BaseException.assertSmaller(requestStartOffset, 0L, "开始Offset不能小于0");
        }
        if (requestStartOffset != null && requestEndOffset != null) {
            BaseException.assertBigger(requestStartOffset, requestEndOffset, "开始Offset不能大于结束Offset");
            BaseException.assertBigger(requestEndOffset - requestStartOffset, maxFetchSize, "单个分区获取的最大数据量不能");
        }
    }

    private Pair<Long, Long> offsetPair(long latestOffset, Long requestStartOffset, Long requestEndOffset) {
        long startOffset;
        long endOffset;
        if (requestStartOffset == null) {
            if (requestEndOffset == null) {
                startOffset = Math.max(latestOffset - maxFetchSize, 0);
                endOffset = latestOffset;
            } else {
                endOffset = requestEndOffset;
                startOffset = Math.max(endOffset - maxFetchSize, 0);
            }
        } else {
            if (requestEndOffset == null) {
                startOffset = requestStartOffset;
                endOffset = Math.min(startOffset + maxFetchSize, latestOffset);
            } else {
                startOffset = requestStartOffset;
                endOffset = requestEndOffset;
            }
        }
        return Pair.of(startOffset, endOffset);
    }

    private List<KafkaData<String, String>> fetchMessage(String bootstrapServers, TopicPartition topicPartition,
                                                         long startOffset, long endOffset) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        List<KafkaData<String, String>> result = Lists.newArrayList();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.assign(Lists.newArrayList(topicPartition));
            consumer.seek(topicPartition, startOffset);

            ConsumerRecords<String, String> records;
            Duration timeout = Duration.ofMillis(ConstantConfig.DEFAULT_TIME_OUT);
            while (!(records = consumer.poll(timeout)).isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    result.add(KafkaData.convertFromConsumerRecord(record));
                    if (record.offset() >= endOffset) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void produce(String clusterName, String topic, String key, String message) {
        Cluster cluster = clusterMapper.getByName(clusterName);
        BaseException.assertNull(cluster, "集群不存在");

        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, cluster.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(topic, key, message));
        }
    }

}
