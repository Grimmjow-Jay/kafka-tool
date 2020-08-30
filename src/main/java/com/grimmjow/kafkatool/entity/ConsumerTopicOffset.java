package com.grimmjow.kafkatool.entity;

import lombok.Data;

/**
 * @author Grimm
 * @date 2020/8/30
 */
@Data
public class ConsumerTopicOffset {

    private Long id;

    private String consumer;

    private String topic;

    private int partition;

    private long offset;

    private long logSize;

    private long lag;

    private long timestamp;

}
