package com.grimmjow.kafkatool.domain.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
     * 时间间隔（秒）
     */
    @NotNull(message = "间隔时间不能为空")
    @Min(value = 1L, message = "间隔时间有误")
    private Long interval;

    /**
     * 最小时间
     */
    @NotNull(message = "最小时间非法")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 最大时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "最大时间非法")
    private Date endTime;

}
