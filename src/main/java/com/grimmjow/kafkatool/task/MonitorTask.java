package com.grimmjow.kafkatool.task;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.mapper.ConsumerTopicOffsetMapper;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Slf4j
public class MonitorTask implements Runnable, MonitorTrigger {

    @Getter
    private MonitorTaskRequest monitorTaskRequest;

    private KafkaClientPool kafkaClientPool;

    private ConsumerTopicOffsetMapper consumerTopicOffsetMapper;

    private AtomicBoolean running;

    @Setter
    private int partitionSize = 20;

    public MonitorTask(MonitorTaskRequest monitorTaskRequest, KafkaClientPool kafkaClientPool, ConsumerTopicOffsetMapper consumerTopicOffsetMapper) {
        this.monitorTaskRequest = monitorTaskRequest;
        this.kafkaClientPool = kafkaClientPool;
        this.consumerTopicOffsetMapper = consumerTopicOffsetMapper;
        this.running = new AtomicBoolean(false);
    }

    @Override
    public long getInterval() {
        return monitorTaskRequest.getInterval();
    }

    @Override
    public void run() {
        boolean canRun = this.running.compareAndSet(false, true);
        if (!canRun) {
            return;
        }
        try {
            List<ConsumerTopicOffsetVo> consumerTopicOffsetVo = consumerTopicOffset();
            saveOffset(consumerTopicOffsetVo);
        } catch (KafkaClientException e) {
            log.error(e.getMessage(), e);
        } finally {
            running.set(false);
        }
    }

    /**
     * 查询监控数据
     */
    private List<ConsumerTopicOffsetVo> consumerTopicOffset() throws KafkaClientException {
        return kafkaClientPool.getClient(monitorTaskRequest.getClusterName())
                .offsets(monitorTaskRequest.getConsumer(), monitorTaskRequest.getTopic());
    }

    /**
     * 保存监控数据
     */
    private void saveOffset(List<ConsumerTopicOffsetVo> consumerTopicOffsetVo) {
        List<ConsumerTopicOffset> consumerTopicOffsetList = consumerTopicOffsetVo.stream()
                .map(ConsumerTopicOffsetVo::convert)
                .collect(Collectors.toList());
        Lists.partition(consumerTopicOffsetList, partitionSize).stream()
                .filter(e -> e != null && !e.isEmpty())
                .forEach(consumerTopicOffsetMapper::saveList);
    }

}
