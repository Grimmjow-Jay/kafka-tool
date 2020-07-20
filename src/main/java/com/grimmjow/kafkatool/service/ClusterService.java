package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.entity.KafkaNode;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public interface ClusterService {

    /**
     * 集群列表
     *
     * @return 集群列表
     */
    List<Cluster> clusters();

    /**
     * 新增集群
     *
     * @param cluster 集群
     */
    void addCluster(Cluster cluster);

    /**
     * 移除集群
     *
     * @param clusterName 集群名
     */
    void removeCluster(String clusterName);

    /**
     * 集群的节点
     *
     * @param clusterName 集群名
     * @return 集群的节点
     */
    List<KafkaNode> listNodes(String clusterName);

    /**
     * 重新连接集群
     *
     * @param clusterName 集群名
     */
    void reconnect(String clusterName);

}
