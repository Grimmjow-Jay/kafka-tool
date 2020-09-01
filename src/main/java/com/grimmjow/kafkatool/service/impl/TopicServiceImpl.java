package com.grimmjow.kafkatool.service.impl;

import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class TopicServiceImpl implements TopicService {

    private final KafkaClientPool kafkaClientPool;

    public TopicServiceImpl(KafkaClientPool kafkaClientPool) {
        this.kafkaClientPool = kafkaClientPool;
    }

    @Override
    public Set<String> topics(String clusterName) {
        try {
            return kafkaClientPool.getClient(clusterName).topics();
        } catch (KafkaClientException e) {
            log.error("获取集群Topic列表异常", e);
            throw new BaseException("获取集群Topic列表异常");
        }
    }

    @Override
    public KafkaTopic detail(String clusterName, String topic) {
        try {
            return kafkaClientPool.getClient(clusterName).topicDetail(topic);
        } catch (KafkaClientException e) {
            log.error("获取集群Topic详情异常", e);
            throw new BaseException("获取集群Topic详情异常");
        }
    }

    @Override
    public void createTopic(CreateTopicRequest request) {
        try {
            kafkaClientPool.getClient(request.getClusterName())
                    .createTopic(request.getTopic(), request.getPartition(), (short) request.getReplication());
        } catch (KafkaClientException e) {
            log.error("创建Topic异常", e);
            throw new BaseException("创建Topic异常");
        }
    }
}
