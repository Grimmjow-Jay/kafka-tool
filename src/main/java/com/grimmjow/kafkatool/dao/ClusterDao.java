package com.grimmjow.kafkatool.dao;

import com.grimmjow.kafkatool.entity.Cluster;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/29
 */
public interface ClusterDao {

    /**
     * 获取所有的集群
     *
     * @return 集群列表
     */
    List<Cluster> getClusterList();

    /**
     * 新增集群
     *
     * @param cluster 集群
     */
    void saveCluster(Cluster cluster);

    /**
     * 删除集群
     */
    void removeCluster(String clusterName);

    /**
     * 根据集群名查询集群
     *
     * @param clusterName 集群名
     * @return 集群
     */
    Cluster getCluster(String clusterName);
}
