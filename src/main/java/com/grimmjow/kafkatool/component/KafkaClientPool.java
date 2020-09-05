package com.grimmjow.kafkatool.component;

import com.google.common.collect.Maps;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.mapper.ClusterMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * @author Grimm
 * @date 2020/8/29
 */
@Component
public class KafkaClientPool {

    private final ClusterMapper clusterMapper;
    private Map<String, KafkaClient> pool = Maps.newConcurrentMap();

    public KafkaClientPool(ClusterMapper clusterMapper) {
        this.clusterMapper = clusterMapper;
    }

    /**
     * 连接至集群，返回连接客户端
     *
     * @param cluster 集群
     * @return 连接客户端
     */
    public KafkaClient connect(Cluster cluster) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, cluster.getBootstrapServers());
        return new KafkaClient(KafkaAdminClient.create(props), cluster);
    }

    /**
     * 获取连接client
     *
     * @param clusterName 集群名
     * @return 连接client
     */
    public KafkaClient getClient(String clusterName) throws KafkaClientException {
        KafkaClient kafkaClient = pool.get(clusterName);
        if (kafkaClient != null) {
            return kafkaClient;
        }
        Cluster cluster = clusterMapper.getByName(clusterName);
        if (cluster == null) {
            throw new KafkaClientException("集群不存在");
        }

        kafkaClient = connect(cluster);
        pool.put(clusterName, kafkaClient);
        return kafkaClient;
    }

    /**
     * 断开连接
     *
     * @param clusterName 集群名
     */
    public void disconnectIfPresent(String clusterName) {
        KafkaClient kafkaClient = pool.get(clusterName);
        if (kafkaClient != null) {
            kafkaClient.close();
            pool.remove(clusterName);
        }
    }

    /**
     * 重新连接
     *
     * @param clusterName 集群名
     */
    public void reconnect(String clusterName) throws KafkaClientException {
        disconnectIfPresent(clusterName);
        getClient(clusterName);
    }
}
