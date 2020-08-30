package com.grimmjow.kafkatool.component;

import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.domain.Cluster;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.mapper.ClusterMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Component
public class ClusterClientPool {

    private final ClusterMapper clusterMapper;
    private Map<String, AdminClient> pool = Maps.newConcurrentMap();

    public ClusterClientPool(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    /**
     * 连接至集群，返回连接客户端
     *
     * @param cluster 集群
     * @return 连接客户端
     */
    public AdminClient connect(Cluster cluster) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, cluster.getBootstrapServers());
        return KafkaAdminClient.create(props);
    }

    /**
     * 获取连接client
     *
     * @param clusterName 集群名
     * @return 连接client
     */
    public AdminClient getClient(String clusterName) {
        AdminClient adminClient = pool.get(clusterName);
        if (adminClient != null) {
            return adminClient;
        }
        Cluster cluster = clusterMapper.getByName(clusterName);
        BaseException.assertNull(cluster, "集群不存在");

        adminClient = connect(cluster);
        pool.put(clusterName, adminClient);
        return adminClient;
    }

    /**
     * 断开连接
     *
     * @param clusterName 集群名
     */
    public void disconnectIfPresent(String clusterName) {
        AdminClient adminClient = pool.get(clusterName);
        if (adminClient != null) {
            adminClient.close();
            pool.remove(clusterName);
        }
    }

    /**
     * 重新连接
     *
     * @param clusterName 集群名
     */
    public void reconnect(String clusterName) {
        disconnectIfPresent(clusterName);
        getClient(clusterName);
    }
}
