package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.entity.request.MonitorRequest;

/**
 * @author Grimm
 * @date 2020/8/29
 */
public interface MonitorService {

    /**
     * 开启一个监控
     *
     * @param monitorRequest 监控请求参数
     */
    void enableMonitor(MonitorRequest monitorRequest);

    /**
     * 禁用一个监控
     *
     * @param monitorRequest 监控请求参数
     */
    void disableMonitor(MonitorRequest monitorRequest);

}
