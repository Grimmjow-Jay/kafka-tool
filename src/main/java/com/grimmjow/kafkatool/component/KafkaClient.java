package com.grimmjow.kafkatool.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.domain.KafkaNode;
import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.KafkaTopicPartition;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @date 2020/8/31
 */
@Slf4j
public class KafkaClient {

    private AdminClient adminClient;

    private long timeout;

    private TimeUnit timeoutUnit;

    private Cluster cluster;

    public KafkaClient(AdminClient adminClient, Cluster cluster, long timeout, TimeUnit timeoutUnit) {
        this.adminClient = adminClient;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.cluster = cluster;
    }

    public KafkaClient(AdminClient adminClient, Cluster cluster) {
        this(adminClient, cluster, DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
    }

    public void close() {
        if (adminClient != null) {
            adminClient.close();
        }
    }

    public List<KafkaNode> listNodes() throws KafkaClientException {
        KafkaFuture<Collection<Node>> nodesFuture = adminClient.describeCluster().nodes();
        Collection<Node> nodeCollection;
        try {
            nodeCollection = nodesFuture.get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
        List<KafkaNode> kafkaNodeList = Lists.newArrayList();
        for (Node node : nodeCollection) {
            kafkaNodeList.add(KafkaNode.convert(node));
        }
        return kafkaNodeList;
    }

    public Set<String> topics() throws KafkaClientException {
        ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(true));
        KafkaFuture<Set<String>> names = listTopicsResult.names();
        try {
            return names.get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
    }

    public KafkaTopic topicDetail(String topic) throws KafkaClientException {
        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Lists.newArrayList(topic));
        KafkaFuture<TopicDescription> topicFutureMap = describeTopicsResult.values().get(topic);
        TopicDescription topicDescription;
        try {
            topicDescription = topicFutureMap.get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
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
            topicPartitionOffsetsMap = listOffsetsResult.all().get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
        topicPartitionOffsetsMap.forEach((k, v) -> partitionMap.get(k.partition()).setOffset(v.offset()));

        return new KafkaTopic(topicDescription.name(), topicDescription.isInternal(), partitionList);
    }

    public void createTopic(String name, int numPartitions, short replicationFactor) throws KafkaClientException {
        ArrayList<NewTopic> newTopics = Lists.newArrayList(new NewTopic(name, numPartitions, replicationFactor));

        CreateTopicsResult createTopicsResult = adminClient.createTopics(newTopics);
        try {
            createTopicsResult.all().get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
    }

    public List<String> consumers() throws KafkaClientException {
        KafkaFuture<Collection<ConsumerGroupListing>> consumerGroupListingFuture = adminClient.listConsumerGroups().all();
        try {
            Collection<ConsumerGroupListing> consumerGroupListings = consumerGroupListingFuture.get(timeout, timeoutUnit);
            List<String> consumers = Lists.newArrayList();
            consumerGroupListings.forEach(e -> consumers.add(e.groupId()));
            return consumers;
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new KafkaClientException(e);
        }
    }

    public List<ConsumerTopicOffsetVo> offsets(String consumerName, String topic) throws KafkaClientException {
        KafkaFuture<TopicDescription> topicDescFuture = adminClient
                .describeTopics(Lists.newArrayList(topic)).values().get(topic);
        TopicDescription topicDescription;
        try {
            topicDescription = topicDescFuture.get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
        List<TopicPartition> topicPartitions = topicDescription.partitions().stream()
                .map(e -> new TopicPartition(topic, e.partition()))
                .collect(Collectors.toList());

        return consumerTopicOffset(consumerName, topicPartitions);
    }

    public List<ConsumerTopicOffsetVo> offsets(String consumerName) throws KafkaClientException {
        return consumerTopicOffset(consumerName, null);
    }

    public List<ConsumerTopicOffsetVo> consumerTopicOffset(String consumerName, List<TopicPartition> topicPartitions) throws KafkaClientException {
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = adminClient
                .listConsumerGroupOffsets(consumerName, new ListConsumerGroupOffsetsOptions().topicPartitions(topicPartitions))
                .partitionsToOffsetAndMetadata();
        Map<TopicPartition, OffsetAndMetadata> topicOffsetAndMetadataMap;
        try {
            topicOffsetAndMetadataMap = mapKafkaFuture.get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
        Map<TopicPartition, ConsumerTopicOffsetVo> consumerTopicOffsetMap = Maps.newLinkedHashMap();

        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = Maps.newHashMap();
        topicOffsetAndMetadataMap.forEach((topicPartition, offsetAndMetadata) -> {
            if (offsetAndMetadata != null) {
                topicPartitionOffsets.put(topicPartition, OffsetSpec.latest());
                consumerTopicOffsetMap.put(topicPartition, ConsumerTopicOffsetVo.builder()
                        .clusterName(this.cluster.getClusterName())
                        .consumer(consumerName)
                        .partition(topicPartition.partition())
                        .topic(topicPartition.topic())
                        .offset(offsetAndMetadata.offset())
                        .build());
            }
        });
        KafkaFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> topicPartitionOffsetsInfo = adminClient
                .listOffsets(topicPartitionOffsets).all();

        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> listOffsetsInfoMap;
        try {
            listOffsetsInfoMap = topicPartitionOffsetsInfo.get(timeout, timeoutUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KafkaClientException(e);
        }
        listOffsetsInfoMap.forEach((k, v) -> {
            ConsumerTopicOffsetVo consumerTopicOffsetVo = consumerTopicOffsetMap.get(k);
            if (consumerTopicOffsetVo != null) {
                consumerTopicOffsetVo.setLogSize(v.offset());
                consumerTopicOffsetVo.setLag(consumerTopicOffsetVo.getLogSize() - consumerTopicOffsetVo.getOffset());
            }
        });
        return Lists.newArrayList(consumerTopicOffsetMap.values());
    }

}
