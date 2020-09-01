package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.component.MonitorTaskPool;
import com.grimmjow.kafkatool.domain.request.MonitorDataRequest;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.mapper.ConsumerTopicOffsetMapper;
import com.grimmjow.kafkatool.service.MonitorService;
import com.grimmjow.kafkatool.task.MonitorTask;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private final MonitorTaskPool monitorTaskPool;

    private final KafkaClientPool kafkaClientPool;

    private final ConsumerTopicOffsetMapper consumerTopicOffsetMapper;


    public MonitorServiceImpl(MonitorTaskPool monitorTaskPool,
                              KafkaClientPool kafkaClientPool,
                              ConsumerTopicOffsetMapper consumerTopicOffsetMapper) {
        this.monitorTaskPool = monitorTaskPool;
        this.kafkaClientPool = kafkaClientPool;
        this.consumerTopicOffsetMapper = consumerTopicOffsetMapper;
    }

    @Override
    public void enableMonitor(MonitorTaskRequest monitorTaskRequest) {
        monitorTaskPool.addTask(new MonitorTask(monitorTaskRequest, kafkaClientPool, consumerTopicOffsetMapper));
    }

    @Override
    public void disableMonitor(MonitorTaskRequest monitorTaskRequest) {
        monitorTaskPool.removeTask(monitorTaskRequest);
    }

    @Override
    public List<ConsumerTopicOffsetVo> offsetData(MonitorDataRequest monitorDataRequest) {
        List<ConsumerTopicOffsetVo> consumerTopicOffsetVoList;

        if (monitorDataRequest.getPartition() == null) {
            consumerTopicOffsetVoList = consumerTopicOffsetMapper.listByIntervalIgnorePartition(
                    monitorDataRequest.getStartTime(),
                    monitorDataRequest.getEndTime(),
                    monitorDataRequest.getClusterName(),
                    monitorDataRequest.getConsumer(),
                    monitorDataRequest.getTopic(),
                    monitorDataRequest.getInterval());
        } else {
            consumerTopicOffsetVoList = consumerTopicOffsetMapper.listByInterval(
                    monitorDataRequest.getStartTime(),
                    monitorDataRequest.getEndTime(),
                    monitorDataRequest.getClusterName(),
                    monitorDataRequest.getConsumer(),
                    monitorDataRequest.getTopic(),
                    monitorDataRequest.getInterval(),
                    monitorDataRequest.getPartition());
        }

        consumerTopicOffsetVoList.forEach(ConsumerTopicOffsetVo::updateLag);

        return fillInterstice(consumerTopicOffsetVoList);
    }

    private List<ConsumerTopicOffsetVo> fillInterstice(List<ConsumerTopicOffsetVo> consumerTopicOffsetVoList) {
        List<ConsumerTopicOffsetVo> filledVoList = Lists.newArrayList();
        if (consumerTopicOffsetVoList.isEmpty()) {
            return filledVoList;
        }
        Iterator<ConsumerTopicOffsetVo> iterator = consumerTopicOffsetVoList.iterator();
        ConsumerTopicOffsetVo first = iterator.next();
        filledVoList.add(first);

        long timestamp = first.getTimestamp();
        ConsumerTopicOffsetVo before = first;
        while (iterator.hasNext()) {
            ConsumerTopicOffsetVo next = iterator.next();
            while (next.getTimestamp() > ++timestamp) {
                ConsumerTopicOffsetVo consumerTopicOffsetVo = ConsumerTopicOffsetVo.builder()
                        .clusterName(before.getClusterName())
                        .consumer(before.getConsumer())
                        .topic(before.getTopic())
                        .partition(before.getPartition())
                        .offset(before.getOffset())
                        .logSize(before.getLogSize())
                        .lag(before.getLag())
                        .timestamp(timestamp)
                        .build();
                filledVoList.add(consumerTopicOffsetVo);
            }

            before = next;
            filledVoList.add(next);
        }

        return filledVoList;
    }
}
