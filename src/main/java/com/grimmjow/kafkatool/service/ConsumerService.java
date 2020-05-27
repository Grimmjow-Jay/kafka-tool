package com.grimmjow.kafkatool.service;

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


    List<String> offsets(String clusterName, String consumerName);
}
