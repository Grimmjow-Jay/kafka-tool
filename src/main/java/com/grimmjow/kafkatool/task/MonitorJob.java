package com.grimmjow.kafkatool.task;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.entity.ConsumerTopicOffset;
import com.grimmjow.kafkatool.entity.MonitorTask;
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
public class MonitorJob implements Runnable, MonitorTrigger {

    @Getter
    private MonitorTask monitorTask;

    private KafkaClientPool kafkaClientPool;

    private ConsumerTopicOffsetMapper consumerTopicOffsetMapper;

    private AtomicBoolean running;

    @Setter
    private int partitionSize = 20;

    public MonitorJob(MonitorTask monitorTask, KafkaClientPool kafkaClientPool, ConsumerTopicOffsetMapper consumerTopicOffsetMapper) {
        this.monitorTask = monitorTask;
        this.kafkaClientPool = kafkaClientPool;
        this.consumerTopicOffsetMapper = consumerTopicOffsetMapper;
        this.running = new AtomicBoolean(false);
    }

    @Override
    public long getInterval() {
        return monitorTask.getInterval();
    }

    @Override
    public void run() {
        boolean canRun = this.running.compareAndSet(false, true);
        if (!canRun) {
            return;
        }
        String logMsg = String.format(
                "执行监控任务[集群：%s，消费者：%s，Topic：%s]",
                monitorTask.getClusterName(),
                monitorTask.getConsumer(),
                monitorTask.getTopic());
        try {
            log.info("开始" + logMsg);
            List<ConsumerTopicOffsetVo> consumerTopicOffsetVo = consumerTopicOffset();
            saveOffset(consumerTopicOffsetVo);
            log.info(logMsg + "结束.");
        } catch (KafkaClientException e) {
            log.error(logMsg + "失败，" + e.getMessage(), e);
        } finally {
            running.set(false);
        }
    }

    /**
     * 查询监控数据
     */
    private List<ConsumerTopicOffsetVo> consumerTopicOffset() throws KafkaClientException {
        return kafkaClientPool.getClient(monitorTask.getClusterName())
                .offsets(monitorTask.getConsumer(), monitorTask.getTopic());
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
