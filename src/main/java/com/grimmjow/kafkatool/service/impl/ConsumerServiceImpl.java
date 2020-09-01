package com.grimmjow.kafkatool.service.impl;

import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.service.ConsumerService;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/27
 */
@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    private final KafkaClientPool kafkaClientPool;

    public ConsumerServiceImpl(KafkaClientPool kafkaClientPool) {
        this.kafkaClientPool = kafkaClientPool;
    }

    @Override
    public List<String> consumers(String clusterName) {
        try {
            return kafkaClientPool.getClient(clusterName).consumers();
        } catch (KafkaClientException e) {
            log.error("获取消费者列表失败", e);
            throw new BaseException("获取消费者列表失败");
        }
    }

    @Override
    public List<ConsumerTopicOffsetVo> offsets(String clusterName, String consumerName) {
        List<ConsumerTopicOffsetVo> topicOffsetVoList;
        try {
            topicOffsetVoList = kafkaClientPool.getClient(clusterName).offsets(consumerName);
        } catch (KafkaClientException e) {
            log.info("获取消费者offset信息失败", e);
            throw new BaseException("获取消费者offset信息失败:" + e.getMessage());
        }
        topicOffsetVoList.sort(Comparator
                .comparing(ConsumerTopicOffsetVo::getTopic)
                .thenComparing(ConsumerTopicOffsetVo::getPartition));
        return topicOffsetVoList;
    }
}
