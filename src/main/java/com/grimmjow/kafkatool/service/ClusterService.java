package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.entity.KafkaNode;
import org.apache.kafka.common.Node;

import java.util.List;

public interface ClusterService {

    List<Cluster> clusters();

    void addCluster(Cluster cluster);

    void removeCluster(String clusterName);

    List<KafkaNode> listNodes(String clusterName);
}
