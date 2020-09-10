package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;
import com.grimmjow.kafkatool.domain.request.FetchMessageRequest;
import com.grimmjow.kafkatool.vo.KafkaData;

import java.util.List;
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

    /**
     * 获取Kafka指定Topic的数据
     *
     * @param clusterName         集群名
     * @param topic               Topic
     * @param fetchMessageRequest 获取数据的相关参数
     * @return Kafka消息数据
     */
    List<KafkaData<String, String>> fetchMessage(String clusterName, String topic, FetchMessageRequest fetchMessageRequest);

    /**
     * 发送消息到指定Topic
     *
     * @param clusterName 集群名
     * @param topic       Topic
     * @param key         消息Key
     * @param message     消息记录
     */
    void produce(String clusterName, String topic, String key, String message);

}
