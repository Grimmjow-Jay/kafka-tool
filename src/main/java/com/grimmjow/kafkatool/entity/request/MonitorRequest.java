package com.grimmjow.kafkatool.entity.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Grimm
 * @date 2020/8/28
 */
@Data
@EqualsAndHashCode
public class MonitorRequest {

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
     * 监控时间间隔（毫秒）
     */
    private Long interval;

}
