package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.component.MonitorTaskPool;
import com.grimmjow.kafkatool.domain.request.MonitorDataRequest;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.entity.MonitorTask;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.mapper.ConsumerTopicOffsetMapper;
import com.grimmjow.kafkatool.mapper.MonitorTaskMapper;
import com.grimmjow.kafkatool.service.MonitorService;
import com.grimmjow.kafkatool.task.MonitorJob;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import com.grimmjow.kafkatool.vo.MonitorTaskVo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private final MonitorTaskPool monitorTaskPool;

    private final KafkaClientPool kafkaClientPool;

    private final ConsumerTopicOffsetMapper consumerTopicOffsetMapper;

    private final MonitorTaskMapper monitorTaskMapper;

    public MonitorServiceImpl(MonitorTaskPool monitorTaskPool,
                              KafkaClientPool kafkaClientPool,
                              ConsumerTopicOffsetMapper consumerTopicOffsetMapper,
                              MonitorTaskMapper monitorTaskMapper) {
        this.monitorTaskPool = monitorTaskPool;
        this.kafkaClientPool = kafkaClientPool;
        this.consumerTopicOffsetMapper = consumerTopicOffsetMapper;
        this.monitorTaskMapper = monitorTaskMapper;
    }

    @Override
    public void addMonitor(MonitorTaskRequest monitorTaskRequest) {
        MonitorTask monitorTask = monitorTaskRequest.convertToMonitorTask();
        monitorTask.setIsActive(true);
        try {
            monitorTaskMapper.save(monitorTask);
        } catch (DuplicateKeyException e) {
            throw new BaseException("监控任务已存在");
        }
        monitorTaskPool.addTask(new MonitorJob(monitorTask, kafkaClientPool, consumerTopicOffsetMapper));
    }

    @Override
    public void removeMonitor(Long id) {
        MonitorTask monitorTask = monitorTaskMapper.findById(id);
        BaseException.assertNull(monitorTask, "监控任务不存在");
        monitorTaskMapper.remove(monitorTask);
        monitorTaskPool.removeTask(monitorTask);
    }

    @Override
    public List<ConsumerTopicOffsetVo> offsetData(MonitorDataRequest monitorDataRequest) {
        List<ConsumerTopicOffsetVo> consumerTopicOffsetVoList;

        long startTime = monitorDataRequest.getStartTime().getTime();
        long endTime = monitorDataRequest.getEndTime().getTime();
        long interval = monitorDataRequest.getInterval() * 1000L;

        if (monitorDataRequest.getPartition() == null) {
            consumerTopicOffsetVoList = consumerTopicOffsetMapper.listByIntervalIgnorePartition(
                    startTime,
                    endTime,
                    monitorDataRequest.getClusterName(),
                    monitorDataRequest.getConsumer(),
                    monitorDataRequest.getTopic(),
                    interval);
        } else {
            consumerTopicOffsetVoList = consumerTopicOffsetMapper.listByInterval(
                    startTime,
                    endTime,
                    monitorDataRequest.getClusterName(),
                    monitorDataRequest.getConsumer(),
                    monitorDataRequest.getTopic(),
                    interval,
                    monitorDataRequest.getPartition());
        }

        consumerTopicOffsetVoList.forEach(ConsumerTopicOffsetVo::updateLag);

        return consumerTopicOffsetVoList;
    }

    private List<ConsumerTopicOffsetVo> fillInterstice(List<ConsumerTopicOffsetVo> consumerTopicOffsetVoList, long interval) {
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
            while (next.getTimestamp() > (timestamp += interval)) {
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

    @Override
    public List<MonitorTaskVo> listMonitorTask(MonitorTaskRequest monitorTaskRequest) {
        List<MonitorTask> monitorTaskList = monitorTaskMapper.list(monitorTaskRequest.getClusterName(),
                monitorTaskRequest.getConsumer(), monitorTaskRequest.getTopic());
        return monitorTaskList.stream()
                .map(MonitorTaskVo::convertFromMonitorTask)
                .collect(Collectors.toList());
    }

    @Override
    public void activeMonitor(Long id) {
        MonitorTask monitorTask = monitorTaskMapper.findById(id);
        BaseException.assertNull(monitorTask, "监控任务ID无效");
        BaseException.assertCondition(monitorTask.getIsActive(), "监控任务已经启用");

        monitorTaskMapper.activeMonitor(id);
        monitorTaskPool.addTask(new MonitorJob(monitorTask, kafkaClientPool, consumerTopicOffsetMapper));
    }

    @Override
    public void disableMonitor(Long id) {
        MonitorTask monitorTask = monitorTaskMapper.findById(id);
        BaseException.assertNull(monitorTask, "监控任务ID无效");
        BaseException.assertCondition(!monitorTask.getIsActive(), "监控任务已经禁用");

        monitorTaskMapper.disableMonitor(id);
        monitorTaskPool.removeTask(monitorTask);
    }

}
