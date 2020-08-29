package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.ClusterClientPool;
import com.grimmjow.kafkatool.dao.ClusterDao;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.entity.KafkaNode;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.ClusterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkatool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class ClusterServiceImpl implements ClusterService {

    private final ClusterClientPool clusterClientPool;

    private final ClusterDao clusterDao;

    public ClusterServiceImpl(ClusterClientPool clusterClientPool, ClusterDao clusterDao) {
        this.clusterClientPool = clusterClientPool;
        this.clusterDao = clusterDao;
    }


    @Override
    public List<Cluster> clusters() {
        return clusterDao.getClusterList();
    }

    @Override
    public void addCluster(Cluster cluster) {
        BaseException.assertNull(cluster, "集群为空");
        BaseException.assertBlank(cluster.getClusterName(), "集群名为空");

        clusterDao.saveCluster(cluster);
    }

    @Override
    public void removeCluster(String clusterName) {
        clusterClientPool.disconnectIfPresent(clusterName);
        clusterDao.removeCluster(clusterName);
    }

    @Override
    public List<KafkaNode> listNodes(String clusterName) {
        AdminClient adminClient = clusterClientPool.getClient(clusterName);
        KafkaFuture<Collection<Node>> nodesFuture = adminClient.describeCluster().nodes();
        Collection<Node> nodeCollection;
        try {
            nodeCollection = nodesFuture.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.info("获取集群节点失败", e);
            throw new BaseException("获取集群节点失败:" + e.getMessage());
        }
        List<KafkaNode> kafkaNodeList = Lists.newArrayList();
        for (Node node : nodeCollection) {
            kafkaNodeList.add(KafkaNode.convert(node));
        }
        return kafkaNodeList;
    }

    @Override
    public void reconnect(String clusterName) {
        clusterClientPool.reconnect(clusterName);
    }

}
