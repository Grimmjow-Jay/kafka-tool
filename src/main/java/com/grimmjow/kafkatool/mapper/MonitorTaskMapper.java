package com.grimmjow.kafkatool.mapper;

import com.grimmjow.kafkatool.entity.MonitorTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Grimm
 * @date 2020/9/7
 */
@Repository
public interface MonitorTaskMapper {

    /**
     * 保存监控任务
     *
     * @param monitorTask 监控任务
     */
    void save(@Param("monitorTask") MonitorTask monitorTask);

    /**
     * 按条件查询监控任务列表
     *
     * @param clusterName 集群名
     * @param consumer    消费者
     * @param topic       Topic
     * @return 监控任务列表
     */
    List<MonitorTask> list(@Param("clusterName") @Nullable String clusterName,
                           @Param("consumer") @Nullable String consumer,
                           @Param("topic") @Nullable String topic);

    /**
     * 查询所有监控任务列表
     *
     * @return 监控任务列表
     */
    default List<MonitorTask> lisAll() {
        return list(null, null, null);
    }

    /**
     * 删除监控任务
     *
     * @param monitorTask 监控任务
     * @return 删除掉的记录数量
     */
    int remove(@Param("monitorTask") MonitorTask monitorTask);

    /**
     * 根据ID查询监控任务
     *
     * @param id 监控任务ID
     * @return 监控任务
     */
    MonitorTask findById(@Param("id") Long id);

    /**
     * 激活监控任务
     *
     * @param id 监控任务ID
     */
    void activeMonitor(@Param("id") Long id);

    /**
     * 禁用监控任务
     *
     * @param id 监控任务ID
     */
    void disableMonitor(@Param("id") Long id);

}
