package com.grimmjow.kafkatool.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.component.ClusterClientPool;
import com.grimmjow.kafkatool.domain.ConsumerTopicOffset;
import com.grimmjow.kafkatool.domain.request.MonitorRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Slf4j
public class MonitorTask implements Runnable, MonitorTrigger {

    @Getter
    private MonitorRequest monitorRequest;

    private ClusterClientPool clusterClientPool;

    private long timeout;

    private TimeUnit unit;

    private AtomicBoolean running;

    public MonitorTask(MonitorRequest monitorRequest, ClusterClientPool clusterClientPool, long timeout, TimeUnit unit) {
        this.monitorRequest = monitorRequest;
        this.clusterClientPool = clusterClientPool;
        this.timeout = timeout;
        this.unit = unit;
        this.running = new AtomicBoolean(false);
    }

    public MonitorTask(MonitorRequest monitorRequest, ClusterClientPool clusterClientPool) {
        this(monitorRequest, clusterClientPool, DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public long getInterval() {
        return monitorRequest.getInterval();
    }

    @Override
    public void run() {
        boolean canRun = this.running.compareAndSet(false, true);
        if (!canRun) {
            return;
        }
        try {
            List<ConsumerTopicOffset> consumerTopicOffset = consumerTopicOffset();
            saveOffset(consumerTopicOffset);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error(e.getMessage(), e);
        } finally {
            running.compareAndSet(true, false);
        }
    }

    private List<ConsumerTopicOffset> consumerTopicOffset() throws ExecutionException, InterruptedException, TimeoutException {
        AdminClient client = clusterClientPool.getClient(monitorRequest.getClusterName());
        String topic = monitorRequest.getTopic();

        KafkaFuture<TopicDescription> topicDescFuture = client
                .describeTopics(Lists.newArrayList(topic)).values().get(topic);
        TopicDescription topicDescription = topicDescFuture.get(timeout, unit);
        List<TopicPartition> topicPartitions = topicDescription.partitions().stream()
                .map(e -> new TopicPartition(topic, e.partition()))
                .collect(Collectors.toList());
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = client
                .listConsumerGroupOffsets(monitorRequest.getConsumer(), new ListConsumerGroupOffsetsOptions().topicPartitions(topicPartitions))
                .partitionsToOffsetAndMetadata();
        Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = mapKafkaFuture.get();

        Map<TopicPartition, ConsumerTopicOffset> consumerTopicOffsetMap = Maps.newLinkedHashMap();
        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = Maps.newHashMap();
        topicPartitionOffsetAndMetadataMap.forEach((topicPartition, offsetAndMetadata) -> {
            topicPartitionOffsets.put(topicPartition, OffsetSpec.latest());
            ConsumerTopicOffset consumerTopicOffset = ConsumerTopicOffset.builder()
                    .consumer(monitorRequest.getConsumer())
                    .partition(topicPartition.partition())
                    .topic(topic)
                    .offset(offsetAndMetadata.offset())
                    .build();
            consumerTopicOffsetMap.put(topicPartition, consumerTopicOffset);
        });
        KafkaFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> topicPartitionOffsetsInfo = client
                .listOffsets(topicPartitionOffsets).all();

        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> listOffsetsInfoMap = topicPartitionOffsetsInfo.get(timeout, unit);
        listOffsetsInfoMap.forEach((k, v) -> {
            ConsumerTopicOffset consumerTopicOffset = consumerTopicOffsetMap.get(k);
            if (consumerTopicOffset != null) {
                consumerTopicOffset.setLogSize(v.offset());
                consumerTopicOffset.setLag(consumerTopicOffset.getLogSize() - consumerTopicOffset.getOffset());
            }
        });

        return Lists.newArrayList(consumerTopicOffsetMap.values());
    }

    private void saveOffset(List<ConsumerTopicOffset> consumerTopicOffset) {
        System.out.println(consumerTopicOffset);
    }

}
