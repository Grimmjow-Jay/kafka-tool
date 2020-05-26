package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.entity.ClusterPool;
import com.grimmjow.kafkatool.entity.KafkaNode;
import com.grimmjow.kafkatool.entity.KafkaTopic;
import com.grimmjow.kafkatool.entity.KafkaTopicPartition;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    @Override
    public Set<String> topics(String clusterName) {
        BaseException.assertTrue(StringUtils.isEmpty(clusterName), "集群名为空");

        AdminClient kafkaAdminClient = ClusterPool.getAdminClient(clusterName);
        ListTopicsResult listTopicsResult = kafkaAdminClient.listTopics(new ListTopicsOptions().listInternal(true));
        KafkaFuture<Set<String>> names = listTopicsResult.names();
        try {
            return names.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取集群Topic列表异常", e);
            throw new BaseException("获取集群Topic列表异常");
        }
    }

    @Override
    public KafkaTopic detail(String clusterName, String topic) {
        BaseException.assertTrue(StringUtils.isEmpty(clusterName), "集群名为空");
        BaseException.assertTrue(StringUtils.isEmpty(topic), "Topic为空");

        AdminClient kafkaAdminClient = ClusterPool.getAdminClient(clusterName);
        DescribeTopicsResult describeTopicsResult = kafkaAdminClient.describeTopics(Lists.newArrayList(topic));
        KafkaFuture<TopicDescription> topicFutureMap = describeTopicsResult.values().get(topic);
        TopicDescription topicDescription;
        try {
            topicDescription = topicFutureMap.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取集群Topic详情异常", e);
            throw new BaseException("获取集群Topic详情异常");
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
        ListOffsetsResult listOffsetsResult = kafkaAdminClient.listOffsets(topicPartitionOffsets);
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> topicPartitionOffsetsMap;
        try {
            topicPartitionOffsetsMap = listOffsetsResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取集群Topic详情异常", e);
            throw new BaseException("获取集群Topic详情异常");
        }
        topicPartitionOffsetsMap.forEach((k, v) -> partitionMap.get(k.partition()).setOffset(v.offset()));

        return new KafkaTopic(topicDescription.name(), topicDescription.isInternal(), partitionList);
    }
}
