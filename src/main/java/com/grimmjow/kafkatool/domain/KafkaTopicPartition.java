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
public class KafkaTopicPartition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分区
     */
    private int partition;

    /**
     * offset
     */
    private long offset;

    /**
     * 副本信息
     */
    private List<KafkaNode> replicas;

    /**
     * leader
     */
    private KafkaNode leader;

}
