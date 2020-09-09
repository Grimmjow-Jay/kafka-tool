package com.grimmjow.kafkatool.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @author Grimm
 * @date 2020/9/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"clusterName", "consumer", "topic"})
public class MonitorTask implements Serializable {

    private static final long serialVersionUID = 1L;

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

}
