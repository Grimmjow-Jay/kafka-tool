package com.grimmjow.kafkatool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Grimm
 * @date 2020/8/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumerTopicOffset {

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
     * 分区
     */
    private int partition;

    /**
     * offset
     */
    private long offset;

    /**
     * logSize
     */
    private long logSize;

    /**
     * lag
     */
    private long lag;

    /**
     * 时间戳
     */
    private long timestamp;

}
