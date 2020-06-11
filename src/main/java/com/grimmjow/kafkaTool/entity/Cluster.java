package com.grimmjow.kafkaTool.entity;

import lombok.Data;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
public class Cluster {

    private String clusterName;

    private String bootstrapServers;

}
