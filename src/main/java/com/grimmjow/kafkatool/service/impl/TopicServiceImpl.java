package com.grimmjow.kafkatool.service.impl;

import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.KafkaTopicPartition;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;
import com.grimmjow.kafkatool.domain.request.FetchDataRequest;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.mapper.ClusterMapper;
import com.grimmjow.kafkatool.service.TopicService;
import com.grimmjow.kafkatool.vo.KafkaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
    public List<KafkaData> fetchMessageData(String clusterName, String topic, FetchDataRequest fetchDataRequest) {
        Cluster cluster = clusterMapper.getByName(clusterName);
        BaseException.assertNull(cluster, "集群不存在");

        KafkaTopic detail = detail(clusterName, topic);
        List<KafkaTopicPartition> partitionList = detail.getPartitions();
        BaseException.assertEmpty(partitionList, "该Topic没有分区信息");

        Collection<TopicPartition> assignPartitions;
        if (fetchDataRequest.getPartition() == null) {
            assignPartitions = partitionList.stream()
                    .map(e -> new TopicPartition(topic, e.getPartition()))
                    .collect(Collectors.toList());
        } else {
            assignPartitions = partitionList.stream()
                    .filter(e -> e.getPartition() == fetchDataRequest.getPartition())
                    .map(e -> new TopicPartition(topic, e.getPartition()))
                    .collect(Collectors.toList());
        }
        BaseException.assertEmpty(assignPartitions, "分区数据有误");

        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, cluster.getBootstrapServers());
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.assign(assignPartitions);

        return null;
    }
}
