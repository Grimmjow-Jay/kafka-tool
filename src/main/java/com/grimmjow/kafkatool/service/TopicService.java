package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;

import java.util.Set;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public interface TopicService {

    /**
     * Topic列表
     *
     * @param clusterName 集群名
     * @return Topic列表
     */
    Set<String> topics(String clusterName);

    /**
     * Topic详情
     *
     * @param clusterName 集群名
     * @param topic       Topic
     * @return Topic详情
     */
    KafkaTopic detail(String clusterName, String topic);

    /**
     * 创建Topic
     *
     * @param createTopicRequest 创建Topic相关参数
     */
    void createTopic(CreateTopicRequest createTopicRequest);
}
