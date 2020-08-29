package com.grimmjow.kafkatool.task;

import com.grimmjow.kafkatool.entity.request.MonitorRequest;
import org.apache.kafka.clients.admin.AdminClient;

/**
 * @author Grimm
 * @date 2020/8/29
 */
public class MonitorTask implements Runnable {

    private MonitorRequest monitorRequest;
    private AdminClient adminClient;

    public MonitorTask(MonitorRequest monitorRequest, AdminClient adminClient) {
        this.monitorRequest = monitorRequest;
        this.adminClient = adminClient;
    }

    public long getInterval() {
        return monitorRequest.getInterval();
    }

    @Override
    public void run() {
    }
}
