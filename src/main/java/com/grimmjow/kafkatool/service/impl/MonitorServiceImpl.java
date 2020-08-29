package com.grimmjow.kafkatool.service.impl;

import com.grimmjow.kafkatool.component.ClusterClientPool;
import com.grimmjow.kafkatool.component.MonitorTaskPool;
import com.grimmjow.kafkatool.entity.request.MonitorRequest;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.MonitorService;
import org.apache.kafka.clients.admin.AdminClient;
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
        BaseException.assertNull(monitorRequest, "参数不能为空");
        BaseException.assertBlank(monitorRequest.getClusterName(), "集群名不能为空");
        BaseException.assertBlank(monitorRequest.getConsumer(), "消费者名不能为空");
        BaseException.assertBlank(monitorRequest.getTopic(), "Topic不能为空");

        AdminClient client = clusterClientPool.getClient(monitorRequest.getClusterName());
        monitorTaskPool.addTask(monitorRequest, client);

    }

    @Override
    public void disableMonitor(MonitorRequest monitorRequest) {
        monitorTaskPool.removeTask(monitorRequest);
    }
}
