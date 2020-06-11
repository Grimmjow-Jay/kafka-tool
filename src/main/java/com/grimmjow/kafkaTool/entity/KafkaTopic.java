package com.grimmjow.kafkaTool.entity;

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

    private String name;

    private boolean internal;

    private List<KafkaTopicPartition> partitions;

}
