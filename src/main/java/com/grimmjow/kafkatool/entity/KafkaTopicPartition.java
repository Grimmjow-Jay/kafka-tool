package com.grimmjow.kafkatool.entity;

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

    private int partition;

    private long offset;

    private List<KafkaNode> replicas;

    private KafkaNode leader;

}
