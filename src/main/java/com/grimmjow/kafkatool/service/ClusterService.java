package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.entity.KafkaNode;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public interface ClusterService {

    List<Cluster> clusters();

    void addCluster(Cluster cluster);

    void removeCluster(String clusterName);

    List<KafkaNode> listNodes(String clusterName);
}
