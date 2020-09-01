package com.grimmjow.kafkatool.service.impl;

import com.grimmjow.kafkatool.component.KafkaClientPool;
import com.grimmjow.kafkatool.domain.KafkaNode;
import com.grimmjow.kafkatool.entity.Cluster;
import com.grimmjow.kafkatool.exception.BaseException;
import com.grimmjow.kafkatool.exception.KafkaClientException;
import com.grimmjow.kafkatool.mapper.ClusterMapper;
import com.grimmjow.kafkatool.service.ClusterService;
import com.grimmjow.kafkatool.vo.ClusterVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@Service
@Slf4j
public class ClusterServiceImpl implements ClusterService {

    private final KafkaClientPool kafkaClientPool;

    private final ClusterMapper clusterMapper;

    public ClusterServiceImpl(KafkaClientPool kafkaClientPool, ClusterMapper clusterMapper) {
        this.kafkaClientPool = kafkaClientPool;
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
        kafkaClientPool.disconnectIfPresent(clusterName);
        clusterMapper.remove(clusterName);
    }

    @Override
    public List<KafkaNode> listNodes(String clusterName) {
        try {
            return kafkaClientPool.getClient(clusterName).listNodes();
        } catch (KafkaClientException e) {
            log.error("获取Kafka节点失败", e);
            throw new BaseException("获取Kafka节点失败");
        }
    }

    @Override
    public void reconnect(String clusterName) {
        try {
            kafkaClientPool.reconnect(clusterName);
        } catch (KafkaClientException e) {
            log.error("重新连接失败", e);
            throw new BaseException("重新连接失败");
        }
    }

}
