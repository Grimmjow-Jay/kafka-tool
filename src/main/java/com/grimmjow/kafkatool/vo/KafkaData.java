package com.grimmjow.kafkatool.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Grimm
 * @date 2020/9/9
 */
@Data
public class KafkaData implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * Key
     */
    private String key;

    /**
     * 数据内容
     */
    private String message;

    /**
     * 时间戳
     */
    private long timestamp;

}
