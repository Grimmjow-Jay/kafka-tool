package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.entity.ClusterPool;
import com.grimmjow.kafkatool.entity.KafkaNode;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.service.ClusterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

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
        BaseException.assertTrue(StringUtils.isEmpty(cluster.getClusterName()), "集群名为空");
        try (AdminClient kafkaAdminClient = ClusterPool.connect(cluster)) {
            KafkaFuture<String> clusterIdFuture = kafkaAdminClient.describeCluster().clusterId();
            String clusterId = clusterIdFuture.get();
            log.info("添加集群：" + clusterId);
        } catch (InterruptedException | ExecutionException e) {
            throw new BaseException("添加集群失败", e);
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
        try {
            Collection<Node> nodeCollection = nodesFuture.get();
            List<KafkaNode> kafkaNodeList = Lists.newArrayList();
            for (Node node : nodeCollection) {
                kafkaNodeList.add(KafkaNode.builder()
                        .id(node.id())
                        .host(node.host())
                        .port(node.port())
                        .rack(node.rack())
                        .idString(node.idString())
                        .build());
            }
            return kafkaNodeList;
        } catch (InterruptedException | ExecutionException e) {
            throw new BaseException("获取集群节点失败");
        }
    }

}
