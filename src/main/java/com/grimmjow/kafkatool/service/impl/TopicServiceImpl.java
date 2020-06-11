package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.entity.ClusterPool;
import com.grimmjow.kafkatool.entity.KafkaNode;
import com.grimmjow.kafkatool.entity.KafkaTopic;
import com.grimmjow.kafkatool.entity.KafkaTopicPartition;
import com.grimmjow.kafkatool.entity.request.CreateTopicRequest;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    @Override
    public Set<String> topics(String clusterName) {
        BaseException.assertBlank(clusterName, "集群名为空");

        AdminClient kafkaAdminClient = ClusterPool.getAdminClient(clusterName);
        ListTopicsResult listTopicsResult = kafkaAdminClient.listTopics(new ListTopicsOptions().listInternal(true));
        KafkaFuture<Set<String>> names = listTopicsResult.names();
        try {
            return names.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("获取集群Topic列表异常", e);
            throw new BaseException("获取集群Topic列表异常:" + e.getMessage());
        }
    }

    @Override
    public KafkaTopic detail(String clusterName, String topic) {
        BaseException.assertBlank(clusterName, "集群名为空");
        BaseException.assertBlank(topic, "Topic为空");

        AdminClient adminClient = ClusterPool.getAdminClient(clusterName);
        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Lists.newArrayList(topic));
        KafkaFuture<TopicDescription> topicFutureMap = describeTopicsResult.values().get(topic);
        TopicDescription topicDescription;
        try {
            topicDescription = topicFutureMap.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("获取集群Topic详情异常", e);
            throw new BaseException("获取集群Topic详情异常:" + e.getMessage());
        }
        List<KafkaTopicPartition> partitionList = Lists.newArrayList();
        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = Maps.newHashMap();
        Map<Integer, KafkaTopicPartition> partitionMap = Maps.newHashMap();
        for (TopicPartitionInfo partitionInfo : topicDescription.partitions()) {
            KafkaTopicPartition kafkaTopicPartition = KafkaTopicPartition.builder()
                    .partition(partitionInfo.partition())
                    .leader(KafkaNode.convert(partitionInfo.leader()))
                    .replicas(KafkaNode.convert(partitionInfo.replicas()))
                    .build();
            partitionList.add(kafkaTopicPartition);
            partitionMap.put(partitionInfo.partition(), kafkaTopicPartition);
            topicPartitionOffsets.put(new TopicPartition(topic, partitionInfo.partition()), OffsetSpec.latest());
        }
        ListOffsetsResult listOffsetsResult = adminClient.listOffsets(topicPartitionOffsets);
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> topicPartitionOffsetsMap;
        try {
            topicPartitionOffsetsMap = listOffsetsResult.all().get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("获取集群Topic详情异常", e);
            throw new BaseException("获取集群Topic详情异常:" + e.getMessage());
        }
        topicPartitionOffsetsMap.forEach((k, v) -> partitionMap.get(k.partition()).setOffset(v.offset()));

        return new KafkaTopic(topicDescription.name(), topicDescription.isInternal(), partitionList);
    }

    @Override
    public void createTopic(CreateTopicRequest request) {
        BaseException.assertNull(request, "请求参数为空");
        String clusterName = request.getClusterName();
        String topic = request.getTopic();
        int partition = request.getPartition();
        int replication = request.getReplication();

        BaseException.assertBlank(clusterName, "集群名为空");
        BaseException.assertBlank(topic, "Topic为空");
        BaseException.assertCondition(partition < 1, "分区数不合法");
        BaseException.assertCondition(replication < 1 || replication > Short.MAX_VALUE, "副本数不合法");

        AdminClient kafkaAdminClient = ClusterPool.getAdminClient(clusterName);

        ArrayList<NewTopic> newTopics = Lists.newArrayList(new NewTopic(topic, partition, (short) replication));
        CreateTopicsResult createTopicsResult = kafkaAdminClient.createTopics(newTopics);
        try {
            createTopicsResult.all().get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("创建Topic异常", e);
            throw new BaseException("创建Topic异常:" + e.getMessage());
        }
    }
}
