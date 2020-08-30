package com.grimmjow.kafkatool.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Grimm
 * @date 2020/8/30
 */
@Data
public class ConsumerTopicOffsetVo {

    private LocalDateTime time;

    private String topic;

    private String consumer;

    private String partition;

    private long offset;

    private long lag;

    private long logSize;

}
