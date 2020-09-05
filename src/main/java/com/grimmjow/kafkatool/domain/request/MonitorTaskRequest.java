package com.grimmjow.kafkatool.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Grimm
 * @date 2020/8/28
 */
@Data
@EqualsAndHashCode
public class MonitorTaskRequest {

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
     * 监控时间间隔（秒）
     */
    @NotNull(message = "间隔时间不能为空")
    @Min(value = 1, message = "间隔时间有误")
    @EqualsAndHashCode.Exclude
    private Long interval;

}
