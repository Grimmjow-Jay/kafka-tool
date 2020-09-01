package com.grimmjow.kafkatool.mapper;

import com.grimmjow.kafkatool.entity.Cluster;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/30
 */
@Repository
public interface ClusterMapper {

    /**
     * 保存集群
     *
     * @param cluster 集群
     */
    void save(@Param("cluster") Cluster cluster);

    /**
     * 查询集群列表
     *
     * @return 集群列表
     */
    List<Cluster> list();

    /**
     * 移除集群
     *
     * @param clusterName 集群名
     * @return 移除的数量
     */
    int remove(@Param("clusterName") String clusterName);

    /**
     * 根据集群名查询集群
     *
     * @param clusterName 集群名
     * @return 集群
     */
    Cluster getByName(@Param("clusterName") String clusterName);

}
