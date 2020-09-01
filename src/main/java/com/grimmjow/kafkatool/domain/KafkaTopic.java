package com.grimmjow.kafkatool.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Topic名
     */
    private String name;

    /**
     * 是否内置Topic
     */
    private boolean internal;

    /**
     * 分区信息
     */
    private List<KafkaTopicPartition> partitions;

}
