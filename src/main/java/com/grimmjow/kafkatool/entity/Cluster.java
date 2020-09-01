package com.grimmjow.kafkatool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cluster {

    private Long id;

    private String clusterName;

    private String bootstrapServers;

}
