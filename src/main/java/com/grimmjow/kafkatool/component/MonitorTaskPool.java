package com.grimmjow.kafkatool.component;

import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.entity.request.MonitorRequest;
import com.grimmjow.kafkatool.task.MonitorTask;
import org.apache.kafka.clients.admin.AdminClient;
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

    private final Map<MonitorRequest, ScheduledFuture<?>> pool = Maps.newConcurrentMap();

    public MonitorTaskPool(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void addTask(MonitorRequest monitorRequest, AdminClient client) {
        MonitorTask task = new MonitorTask(monitorRequest, client);
        Long interval = monitorRequest.getInterval();
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(task, interval);
        pool.put(monitorRequest, scheduledFuture);
    }

    public void removeTask(MonitorRequest monitorRequest) {
        ScheduledFuture<?> scheduledFuture = pool.get(monitorRequest);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            pool.remove(monitorRequest);
        }
    }
}
