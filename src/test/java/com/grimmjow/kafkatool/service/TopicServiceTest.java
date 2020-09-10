package com.grimmjow.kafkatool.service;

import com.grimmjow.kafkatool.TestAbstract;
import com.grimmjow.kafkatool.domain.request.FetchMessageRequest;
import com.grimmjow.kafkatool.vo.KafkaData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Grimm
 * @date 2020/9/10
 */
public class TopicServiceTest extends TestAbstract {

    @Autowired
    private TopicService topicService;

    @Test
    public void fetchMessageData() {
        String clusterName = "local_203";
        String topic = "grimm_test_topic";
        FetchMessageRequest fetchMessageRequest = new FetchMessageRequest();
        fetchMessageRequest.setPartition(0);
        fetchMessageRequest.setStartOffset(5L);
        List<KafkaData<String, String>> kafkaDataList = topicService.fetchMessage(clusterName, topic, fetchMessageRequest);
        kafkaDataList.forEach(System.out::println);
    }

    @Test
    public void produce() {
        String clusterName = "local_203";
        String topic = "grimm_test_topic";
        String key = "produce_key";
        String message = "produce_message" + System.currentTimeMillis();
        topicService.produce(clusterName, topic, key, message);
    }

}
