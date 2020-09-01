package com.grimmjow.kafkatool.domain.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Grimm
 * @date 2020/8/31
 */
@Data
public class AddClusterRequest {

    /**
     * 集群名
     */
    @NotBlank(message = "集群名不能为空")
    private String clusterName;

    /**
     * BootstrapServers
     */
    @NotBlank(message = "BootstrapServers不能为空")
    private String bootstrapServers;

}
