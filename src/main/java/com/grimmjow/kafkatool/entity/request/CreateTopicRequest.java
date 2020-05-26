package com.grimmjow.kafkatool.entity.request;

import lombok.Data;

@Data
public class CreateTopicRequest {

    private String clusterName;

    private String topic;

    private int partition;

    private int replication;

}
