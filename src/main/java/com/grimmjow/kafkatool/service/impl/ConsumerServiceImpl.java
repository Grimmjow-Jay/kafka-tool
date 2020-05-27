package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.entity.ClusterPool;
import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @since 2020/5/27
 */
@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    @Override
    public List<String> consumers(String clusterName) {
        BaseException.assertNull(clusterName, "集群名为空");

        AdminClient adminClient = ClusterPool.getAdminClient(clusterName);
        KafkaFuture<Collection<ConsumerGroupListing>> consumerGroupListingFuture = adminClient.listConsumerGroups().all();
        try {
            Collection<ConsumerGroupListing> consumerGroupListings = consumerGroupListingFuture.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
            List<String> consumers = Lists.newArrayList();
            consumerGroupListings.forEach(e -> consumers.add(e.groupId()));
            return consumers;
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            log.error("获取消费者列表失败", e);
            throw new BaseException("获取消费者列表失败:" + e.getMessage());
        }
    }

    @Override
    public List<ConsumerTopicOffset> offsets(String clusterName, String consumerName) {
        BaseException.assertNull(clusterName, "集群名为空");
        BaseException.assertNull(consumerName, "消费者为空");

        AdminClient adminClient = ClusterPool.getAdminClient(clusterName);

        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = adminClient
                .listConsumerGroupOffsets(consumerName).partitionsToOffsetAndMetadata();
        Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap;
        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = Maps.newHashMap();

        Map<TopicPartition, ConsumerTopicOffset> consumerTopicOffsetMap = Maps.newLinkedHashMap();
        try {
            offsetAndMetadataMap = mapKafkaFuture.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.info("获取消费者信息失败", e);
            throw new BaseException("获取消费者信息失败:" + e.getMessage());
        }
        offsetAndMetadataMap.forEach((k, v) -> {
            topicPartitionOffsets.put(k, OffsetSpec.latest());
            consumerTopicOffsetMap.put(k, ConsumerTopicOffset.builder()
                    .consumer(consumerName)
                    .topic(k.topic())
                    .partition(k.partition())
                    .offset(v.offset())
                    .build());
        });
        KafkaFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> topicPartitionOffsetsInfo = adminClient
                .listOffsets(topicPartitionOffsets).all();
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> listOffsetsInfoMap;
        try {
            listOffsetsInfoMap = topicPartitionOffsetsInfo.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.info("获取消费者信息失败", e);
            throw new BaseException("获取消费者信息失败:" + e.getMessage());
        }
        listOffsetsInfoMap.forEach((k, v) -> {
            ConsumerTopicOffset consumerTopicOffset = consumerTopicOffsetMap.get(k);
            if (consumerTopicOffset != null) {
                consumerTopicOffset.setLogSize(v.offset());
                consumerTopicOffset.setLag(consumerTopicOffset.getLogSize() - consumerTopicOffset.getOffset());
            }
        });

        List<ConsumerTopicOffset> consumerTopicOffsets = Lists.newArrayList(consumerTopicOffsetMap.values());
        consumerTopicOffsets.sort(Comparator
                .comparing(ConsumerTopicOffset::getTopic)
                .thenComparing(ConsumerTopicOffset::getPartition));
        return consumerTopicOffsets;
    }
}
