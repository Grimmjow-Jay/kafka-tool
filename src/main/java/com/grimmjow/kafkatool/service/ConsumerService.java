package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/27
 */
public interface ConsumerService {

    /**
     * 集群的消费者列表
     *
     * @param clusterName 集群名
     * @return 消费者列表
     */
    List<String> consumers(String clusterName);


    /**
     * 消费的offset信息
     *
     * @param clusterName  集群名
     * @param consumerName 消费者名
     * @return 消费的offset信息
     */
    List<ConsumerTopicOffset> offsets(String clusterName, String consumerName);

}
