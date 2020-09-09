package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.domain.request.MonitorDataRequest;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import com.grimmjow.kafkatool.vo.MonitorTaskVo;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/29
 */
public interface MonitorService {

    /**
     * 添加一个监控
     *
     * @param monitorTaskRequest 监控请求参数
     */
    void addMonitor(MonitorTaskRequest monitorTaskRequest);

    /**
     * 删除一个监控
     *
     * @param id 监控任务ID
     */
    void removeMonitor(Long id);

    /**
     * 查询监控数据
     *
     * @param monitorDataRequest 查询条件参数
     * @return 监控数据
     */
    List<ConsumerTopicOffsetVo> offsetData(MonitorDataRequest monitorDataRequest);

    /**
     * 查询监控任务列表
     *
     * @param monitorTaskRequest 查询条件参数
     * @return 监控任务列表
     */
    List<MonitorTaskVo> listMonitorTask(MonitorTaskRequest monitorTaskRequest);

    /**
     * 激活监控任务
     *
     * @param id 监控任务ID
     */
    void activeMonitor(Long id);


    /**
     * 禁用监控任务
     *
     * @param id 监控任务ID
     */
    void disableMonitor(Long id);

}
