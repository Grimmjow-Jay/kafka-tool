package com.grimmjow.kafkatool.service.impl;

import com.grimmjow.kafkatool.component.ClusterClientPool;
import com.grimmjow.kafkatool.component.MonitorTaskPool;
import com.grimmjow.kafkatool.domain.request.MonitorRequest;
import com.grimmjow.kafkatool.service.MonitorService;
import com.grimmjow.kafkatool.task.MonitorTask;
import org.springframework.stereotype.Service;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    private final MonitorTaskPool monitorTaskPool;

    private final ClusterClientPool clusterClientPool;

    public MonitorServiceImpl(MonitorTaskPool monitorTaskPool, ClusterClientPool clusterClientPool) {
        this.monitorTaskPool = monitorTaskPool;
        this.clusterClientPool = clusterClientPool;
    }

    @Override
    public void enableMonitor(MonitorRequest monitorRequest) {
        monitorTaskPool.addTask(new MonitorTask(monitorRequest, clusterClientPool));

    }

    @Override
    public void disableMonitor(MonitorRequest monitorRequest) {
        monitorTaskPool.removeTask(monitorRequest);
    }
}
