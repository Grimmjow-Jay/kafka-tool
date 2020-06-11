package com.grimmjow.kafkaTool.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.grimmjow.kafkaTool.config.ConstantConfig;
import com.grimmjow.kafkaTool.exception.BaseException;
import com.grimmjow.kafkaTool.util.JsonUtil;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.grimmjow.kafkaTool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkaTool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class ClusterPool {

    private static final String CLUSTER_JSON = "cluster.json";
    private static final File CLUSTER_JSON_FILE;
    private static Map<String, Cluster> clusterMap;
    private static Map<String, AdminClient> pool;

    static {
        CLUSTER_JSON_FILE = new File(ConstantConfig.DATA_PATH, CLUSTER_JSON);
        clusterMap = Maps.newLinkedHashMap();
        if (CLUSTER_JSON_FILE.exists() && CLUSTER_JSON_FILE.isFile()) {
            List<Cluster> clusterList = JsonUtil.loadToList(CLUSTER_JSON_FILE, Cluster.class);
            clusterList.forEach(e -> clusterMap.put(e.getClusterName(), e));
        }
        pool = Maps.newConcurrentMap();
    }

    private ClusterPool() {
    }

    public static List<Cluster> getClusterList() {
        return Lists.newArrayList(clusterMap.values());
    }

    public static void addCluster(Cluster cluster) {
        String clusterName = cluster.getClusterName();
        BaseException.assertCondition(clusterMap.containsKey(clusterName), "集群名已存在");
        clusterMap.put(clusterName, cluster);
        saveCluster();
    }

    public static void removeCluster(String clusterName) {
        Cluster cluster = clusterMap.get(clusterName);
        BaseException.assertNull(cluster, "集群不存在");
        clusterMap.remove(clusterName);
        saveCluster();
        AdminClient client = pool.get(clusterName);
        if (client != null) {
            pool.remove(clusterName);
            client.close(Duration.ofMillis(DEFAULT_TIME_UNIT.toMillis(DEFAULT_TIME_OUT)));
        }
    }

    private static void saveCluster() {
        try (FileWriter writer = new FileWriter(CLUSTER_JSON_FILE, false)) {
            writer.write(JsonUtil.objToJson(clusterMap.values()));
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    public static AdminClient getAdminClient(String clusterName) {
        Cluster cluster = clusterMap.get(clusterName);
        BaseException.assertNull(cluster, "集群不存在");
        return pool.computeIfAbsent(clusterName, e -> connect(cluster));
    }

    public static AdminClient connect(Cluster cluster) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, cluster.getBootstrapServers());
        return KafkaAdminClient.create(props);
    }

}