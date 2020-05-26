package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.entity.KafkaTopic;
import com.grimmjow.kafkatool.entity.request.CreateTopicRequest;

import java.util.Set;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public interface TopicService {

    Set<String> topics(String clusterName);

    KafkaTopic detail(String clusterName, String topic);

    void createTopic(CreateTopicRequest createTopicRequest);
}
