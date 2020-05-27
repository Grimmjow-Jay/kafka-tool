package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.entity.ClusterPool;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.ConsumerService;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @since 2020/5/27
 */
@Service
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
            throw new BaseException("获取消费者列表失败:" + e.getMessage());
        }
    }

    @Override
    public List<String> offsets(String clusterName, String consumerName) {
        BaseException.assertNull(clusterName, "集群名为空");
        BaseException.assertNull(consumerName, "消费者为空");

        AdminClient adminClient = ClusterPool.getAdminClient(clusterName);
        DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(
                Lists.newArrayList(consumerName), new DescribeConsumerGroupsOptions().includeAuthorizedOperations(true));
        KafkaFuture<ConsumerGroupDescription> descriptionFuture = describeConsumerGroupsResult.describedGroups().get(consumerName);
        try {
            ConsumerGroupDescription consumerGroupDescription = descriptionFuture.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
            System.out.println(consumerGroupDescription);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}
