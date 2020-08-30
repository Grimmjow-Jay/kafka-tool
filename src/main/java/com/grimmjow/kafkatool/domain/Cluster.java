package com.grimmjow.kafkatool.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
public class Cluster {

    private Long id;

    @NotBlank(message = "集群名不能为空")
    private String clusterName;

    private String bootstrapServers;

}
