package com.grimmjow.kafkatool.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Grimm
 * @date 2020/9/1
 */
@Data
public class MonitorDataRequest {

    /**
     * 集群名
     */
    @NotBlank(message = "集群名不能为空")
    private String clusterName;

    /**
     * 消费者
     */
    @NotBlank(message = "消费者名不能为空")
    private String consumer;

    /**
     * Topic
     */
    @NotBlank(message = "Topic不能为空")
    private String topic;

    /**
     * 分区
     */
    private Integer partition;

    /**
     * 时间间隔（毫秒）
     */
    @NotNull(message = "间隔时间不能为空")
    private Long interval;

    /**
     * 最小时间
     */
    @NotNull(message = "最小时间非法")
    private Long startTime;

    /**
     * 最大时间
     */
    @NotNull(message = "最大时间非法")
    private Long endTime;

}
