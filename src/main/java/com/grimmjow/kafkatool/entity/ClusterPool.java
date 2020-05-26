package com.grimmjow.kafkatool.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.config.ConstantConfig;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.util.JsonUtil;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class ClusterPool {

    private static final String CLUSTER_JSON = "cluster.json";
    private static final File CLUSTER_JSON_FILE;
    private static List<Cluster> clusterList;
    private static Map<String, Cluster> clusterMap;
    private static Map<String, AdminClient> pool;

    static {
        CLUSTER_JSON_FILE = new File(ConstantConfig.DATA_PATH, CLUSTER_JSON);
        clusterList = CLUSTER_JSON_FILE.exists()
                ? JsonUtil.loadToList(CLUSTER_JSON_FILE, Cluster.class)
                : Lists.newArrayList();
        clusterMap = Maps.newConcurrentMap();
        clusterList.forEach(e -> clusterMap.put(e.getClusterName(), e));
        pool = Maps.newConcurrentMap();
    }

    private ClusterPool() {
    }

    public static synchronized List<Cluster> getClusterList() {
        return clusterList;
    }

    public static Cluster getCluster(String clusterName) {
        Cluster cluster = clusterMap.get(clusterName);
        if (cluster != null) {
            return cluster;
        }
        throw new BaseException("集群: " + clusterName + " 不存在");
    }


    public static void addCluster(Cluster cluster) {
        boolean nameExist = checkClusterNameExist(cluster.getClusterName());
        BaseException.assertTrue(nameExist, "集群名已存在");
        clusterList.add(cluster);
        saveCluster();
    }

    public static void removeCluster(String clusterName) {
        boolean removed = clusterList.removeIf(cluster -> Objects.equals(cluster.getClusterName(), clusterName));
        if (removed) {
            clusterMap.remove(clusterName);
            saveCluster();
            AdminClient client = pool.get(clusterName);
            if (client != null) {
                pool.remove(clusterName);
                client.close();
            }
        }
    }

    private static boolean checkClusterNameExist(String clusterName) {
        return clusterMap.containsKey(clusterName);
    }

    private static void saveCluster() {
        try (FileWriter writer = new FileWriter(CLUSTER_JSON_FILE, false)) {
            writer.write(JsonUtil.objToJson(clusterList));
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    public static AdminClient getAdminClient(String clusterName) {
        boolean nameExist = checkClusterNameExist(clusterName);
        BaseException.assertTrue(!nameExist, "集群不存在");
        AdminClient adminClient = pool.get(clusterName);
        if (adminClient == null) {
            adminClient = connect(getCluster(clusterName));
            pool.put(clusterName, adminClient);
        }
        return adminClient;
    }

    public static AdminClient connect(Cluster cluster) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, cluster.getBootstrapServers());
        return KafkaAdminClient.create(props);
    }

}
