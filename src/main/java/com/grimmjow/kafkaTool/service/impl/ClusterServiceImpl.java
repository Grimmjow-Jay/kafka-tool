package com.grimmjow.kafkaTool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkaTool.entity.Cluster;
import com.grimmjow.kafkaTool.entity.ClusterPool;
import com.grimmjow.kafkaTool.entity.KafkaNode;
import com.grimmjow.kafkaTool.exception.BaseException;
import com.grimmjow.kafkaTool.service.ClusterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.grimmjow.kafkaTool.config.ConstantConfig.DEFAULT_TIME_OUT;
import static com.grimmjow.kafkaTool.config.ConstantConfig.DEFAULT_TIME_UNIT;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class ClusterServiceImpl implements ClusterService {

    @Override
    public List<Cluster> clusters() {
        return ClusterPool.getClusterList();
    }

    @Override
    public void addCluster(Cluster cluster) {
        BaseException.assertNull(cluster, "集群为空");
        BaseException.assertBlank(cluster.getClusterName(), "集群名为空");
        try (AdminClient kafkaAdminClient = ClusterPool.connect(cluster)) {
            KafkaFuture<String> clusterIdFuture = kafkaAdminClient.describeCluster().clusterId();
            String clusterId = clusterIdFuture.get(DEFAULT_TIME_OUT, DEFAULT_TIME_UNIT);
            log.info("添加集群：" + clusterId);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.info("添加集群失败", e);
            throw new BaseException("添加集群失败:" + e.getMessage(), e);
        }

        ClusterPool.addCluster(cluster);
    }

    @Override
    public void removeCluster(String clusterName) {
        ClusterPool.removeCluster(clusterName);
    }

    @Override
    public List<KafkaNode> listNodes(String clusterName) {
        AdminClient adminClient = ClusterPool.getAdminClient(clusterName);
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

}
