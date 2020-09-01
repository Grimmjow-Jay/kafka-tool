package com.grimmjow.kafkatool.component;

import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.task.MonitorTask;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Component
public class MonitorTaskPool {

    private final TaskScheduler taskScheduler;

    private final Map<MonitorTaskRequest, ScheduledFuture<?>> pool = Maps.newConcurrentMap();

    public MonitorTaskPool(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * 添加监控任务
     *
     * @param monitorTask 监控任务
     */
    public void addTask(MonitorTask monitorTask) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(monitorTask, monitorTask.getInterval());
        pool.put(monitorTask.getMonitorTaskRequest(), scheduledFuture);
    }

    /**
     * 移除监控任务
     *
     * @param monitorTaskRequest 监控请求信息
     */
    public void removeTask(MonitorTaskRequest monitorTaskRequest) {
        ScheduledFuture<?> scheduledFuture = pool.get(monitorTaskRequest);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            pool.remove(monitorTaskRequest);
        }
    }
}
