package com.grimmjow.kafkatool.entity;

import lombok.Data;

@Data
public class Cluster {

    private String clusterName;

    private String bootstrapServers;

}
