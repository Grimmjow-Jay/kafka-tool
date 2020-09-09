package com.grimmjow.kafkatool.component;

import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.entity.MonitorTask;
import com.grimmjow.kafkatool.mapper.ConsumerTopicOffsetMapper;
import com.grimmjow.kafkatool.mapper.MonitorTaskMapper;
import com.grimmjow.kafkatool.task.MonitorJob;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Component
public class MonitorTaskPool {

    private final TaskScheduler taskScheduler;

    private final MonitorTaskMapper monitorTaskMapper;

    private final KafkaClientPool kafkaClientPool;

    private final ConsumerTopicOffsetMapper consumerTopicOffsetMapper;

    private final Map<MonitorTask, ScheduledFuture<?>> pool = Maps.newConcurrentMap();

    public MonitorTaskPool(TaskScheduler taskScheduler,
                           MonitorTaskMapper monitorTaskMapper,
                           KafkaClientPool kafkaClientPool,
                           ConsumerTopicOffsetMapper consumerTopicOffsetMapper) {
        this.taskScheduler = taskScheduler;
        this.monitorTaskMapper = monitorTaskMapper;
        this.kafkaClientPool = kafkaClientPool;
        this.consumerTopicOffsetMapper = consumerTopicOffsetMapper;
    }

    @PostConstruct
    public void init() {
        List<MonitorTask> monitorTaskList = monitorTaskMapper.lisAll();
        for (MonitorTask monitorTask : monitorTaskList) {
            if (monitorTask.getIsActive()) {
                addTask(new MonitorJob(monitorTask, kafkaClientPool, consumerTopicOffsetMapper));
            }
        }
    }

    /**
     * 添加监控任务
     *
     * @param monitorJob 监控任务
     */
    public void addTask(MonitorJob monitorJob) {
        long period = monitorJob.getInterval() * 1000L;
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(monitorJob, period);
        pool.put(monitorJob.getMonitorTask(), scheduledFuture);
    }

    /**
     * 移除监控任务
     *
     * @param monitorTask 监控任务
     */
    public void removeTask(MonitorTask monitorTask) {
        ScheduledFuture<?> scheduledFuture = pool.get(monitorTask);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            pool.remove(monitorTask);
        }
    }

}
