package com.grimmjow.kafkatool.vo;

import com.grimmjow.kafkatool.entity.Cluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Grimm
 * @date 2020/8/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterVo {

    private Long id;

    private String clusterName;

    private String bootstrapServers;

    public static ClusterVo convert(Cluster cluster) {
        return ClusterVo.builder()
                .id(cluster.getId())
                .clusterName(cluster.getClusterName())
                .bootstrapServers(cluster.getBootstrapServers())
                .build();
    }
}
