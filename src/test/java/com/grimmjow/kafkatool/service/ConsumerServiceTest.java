package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.TestAbstract;
import com.grimmjow.kafkatool.domain.request.EditOffsetRequest;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/9/10
 */
public class ConsumerServiceTest extends TestAbstract {

    @Autowired
    private ConsumerService consumerService;

    @Test
    public void offsets() {
        String clusterName = "local_203";
        String consumerName = "grimm_test0909";
        List<ConsumerTopicOffsetVo> offsets = consumerService.offsets(clusterName, consumerName);
        offsets.forEach(System.out::println);
    }

    @Test
    public void editOffset() {
        String clusterName = "local_203";
        String consumerName = "order";
        EditOffsetRequest editOffsetRequest = new EditOffsetRequest();
        editOffsetRequest.setTopic("BUSINESS_VIRTUAL_ORDER_TO_BE_FULFILLED");
        editOffsetRequest.setPartition(0);
        editOffsetRequest.setOffset(2L);
        consumerService.editOffset(clusterName, consumerName, editOffsetRequest);
    }
}