package com.grimmjow.kafkatool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumerTopicOffset implements Serializable {

    private static final long serialVersionUID = 1L;

    private String consumer;

    private String topic;

    private int partition;

    private long offset;

    private long logSize;

    private long lag;

}
