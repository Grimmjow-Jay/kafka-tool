package com.grimmjow.kafkatool.entity.request;

import lombok.Data;

/**
 * 创建Topic请求参数
 *
 * @author Grimm
 * @since 2020/5/26
 */
@Data
public class CreateTopicRequest {

    /**
     * 集群名
     */
    private String clusterName;

    /**
     * Topic
     */
    private String topic;

    /**
     * 分区数
     */
    private int partition;

    /**
     * 副本数
     */
    private int replication;

}
