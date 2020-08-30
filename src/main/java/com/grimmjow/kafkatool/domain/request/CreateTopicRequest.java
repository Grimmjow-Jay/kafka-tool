package com.grimmjow.kafkatool.domain.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
    @NotBlank(message = "集群名不能为空")
    private String clusterName;

    /**
     * Topic
     */
    @NotBlank(message = "Topic不能为空")
    private String topic;

    /**
     * 分区数
     */
    @Min(message = "分区数不合法", value = 1)
    private int partition;

    /**
     * 副本数
     */
    @Min(message = "副本数不合法", value = 1)
    @Max(message = "副本数不合法", value = Short.MAX_VALUE)
    private int replication;

}
