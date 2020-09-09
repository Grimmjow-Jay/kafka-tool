package com.grimmjow.kafkatool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cluster implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String clusterName;

    private String bootstrapServers;

}
