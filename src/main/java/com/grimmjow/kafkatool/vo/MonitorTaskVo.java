package com.grimmjow.kafkatool.vo;

import com.grimmjow.kafkatool.entity.MonitorTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Grimm
 * @date 2020/9/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitorTaskVo implements Serializable {

    private Long id;

    /**
     * 集群名
     */
    private String clusterName;

    /**
     * 消费者
     */
    private String consumer;

    /**
     * Topic
     */
    private String topic;

    /**
     * 监控时间间隔
     */
    private Long interval;

    /**
     * 是否启用
     */
    private Boolean isActive;

    public static MonitorTaskVo convertFromMonitorTask(MonitorTask monitorTask) {
        if (monitorTask == null) {
            return null;
        }
        return MonitorTaskVo.builder()
                .id(monitorTask.getId())
                .clusterName(monitorTask.getClusterName())
                .consumer(monitorTask.getConsumer())
                .topic(monitorTask.getTopic())
                .interval(monitorTask.getInterval())
                .isActive(monitorTask.getIsActive())
                .build();
    }

}
