package com.grimmjow.kafkatool.dao.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.config.ConstantConfig;
import com.grimmjow.kafkatool.dao.ClusterDao;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Repository
public class ClusterFileDaoImpl implements ClusterDao {

    @Value("${cluster.file.name:cluster.json}")
    private String clusterFileName;

    private File clusterFile;

    @PostConstruct
    public void init() {
        clusterFile = new File(ConstantConfig.DATA_PATH, clusterFileName);
    }

    @Override
    public List<Cluster> getClusterList() {
        if (clusterFile.exists() && clusterFile.isFile()) {
            return JsonUtil.loadToList(clusterFile, Cluster.class);
        }
        return Lists.newArrayList();
    }

    @Override
    public void saveCluster(Cluster cluster) {
        List<Cluster> clusterList = getClusterList();
        for (Cluster savedCluster : clusterList) {
            BaseException.assertCondition(savedCluster.getClusterName().equals(cluster.getClusterName()), "集群名已存在");
        }
        clusterList.add(cluster);
        saveFile(clusterList);
    }

    @Override
    public void removeCluster(String clusterName) {
        List<Cluster> clusterList = getClusterList();
        boolean removed = clusterList.removeIf(cluster -> cluster.getClusterName().equals(clusterName));
        if (removed) {
            saveFile(clusterList);
        }
    }

    @Override
    public Cluster getCluster(String clusterName) {
        List<Cluster> clusterList = getClusterList();
        for (Cluster cluster : clusterList) {
            if (cluster.getClusterName().equals(clusterName)) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * 把集群列表保存到文件中
     *
     * @param clusterList 集群列表
     */
    private void saveFile(List<Cluster> clusterList) {
        try (FileWriter writer = new FileWriter(clusterFile, false)) {
            writer.write(JsonUtil.objToJson(clusterList));
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

}
