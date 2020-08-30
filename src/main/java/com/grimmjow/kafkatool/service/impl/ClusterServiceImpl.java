package com.grimmjow.kafkatool.service.impl;

import com.google.common.collect.Lists;
import com.grimmjow.kafkatool.component.ClusterClientPool;
import com.grimmjow.kafkatool.domain.Cluster;
import com.grimmjow.kafkatool.domain.KafkaNode;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.mapper.ClusterMapper;
import com.grimmjow.kafkatool.service.ClusterService;
import com.grimmjow.kafkatool.vo.ClusterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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

    private final ClusterMapper clusterMapper;

    public ClusterServiceImpl(ClusterClientPool clusterClientPool, ClusterMapper clusterMapper) {
        this.clusterClientPool = clusterClientPool;
        this.clusterMapper = clusterMapper;
    }

    @Override
    public List<ClusterVo> clusters() {
        return clusterMapper.list()
                .stream()
                .map(ClusterVo::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void addCluster(Cluster cluster) {
        clusterMapper.save(cluster);
    }

    @Override
    public void removeCluster(String clusterName) {
        clusterClientPool.disconnectIfPresent(clusterName);
        clusterMapper.remove(clusterName);
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
