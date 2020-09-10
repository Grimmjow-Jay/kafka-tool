package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.domain.request.EditOffsetRequest;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/27
 */
public interface ConsumerService {

    /**
     * 集群的消费者列表
     *
     * @param clusterName 集群名
     * @return 消费者列表
     */
    List<String> consumers(String clusterName);


    /**
     * 消费的offset信息
     *
     * @param clusterName  集群名
     * @param consumerName 消费者名
     * @return 消费的offset信息
     */
    List<ConsumerTopicOffsetVo> offsets(String clusterName, String consumerName);

    /**
     * 编辑Offset位置
     *
     * @param clusterName       集群名
     * @param consumerName      消费者
     * @param editOffsetRequest 包含Topic、分区、offset的请求信息
     */
    void editOffset(String clusterName, String consumerName, EditOffsetRequest editOffsetRequest);

}
