package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.domain.request.MonitorDataRequest;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/29
 */
public interface MonitorService {

    /**
     * 开启一个监控
     *
     * @param monitorTaskRequest 监控请求参数
     */
    void enableMonitor(MonitorTaskRequest monitorTaskRequest);

    /**
     * 禁用一个监控
     *
     * @param monitorTaskRequest 监控请求参数
     */
    void disableMonitor(MonitorTaskRequest monitorTaskRequest);

    /**
     * 查询监控数据
     *
     * @param monitorDataRequest 查询条件参数
     * @return 监控数据
     */
    List<ConsumerTopicOffsetVo> offsetData(MonitorDataRequest monitorDataRequest);

}
