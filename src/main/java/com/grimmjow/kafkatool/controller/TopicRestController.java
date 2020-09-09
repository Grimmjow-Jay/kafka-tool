package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;
import com.grimmjow.kafkatool.domain.request.FetchDataRequest;
import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import com.grimmjow.kafkatool.service.TopicService;
import com.grimmjow.kafkatool.vo.KafkaData;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@RestController
@RequestMapping("topic")
public class TopicRestController {

    private final TopicService topicService;

    public TopicRestController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/topics/{clusterName}")
    public ResponseEntity<Set<String>> topics(@NotBlank(message = "集群名不能为空") @PathVariable("clusterName") String clusterName) {
        return ResponseEntity.success(topicService.topics(clusterName));
    }

    @GetMapping("/detail/{clusterName}/{topic}")
    public ResponseEntity<KafkaTopic> detail(@NotBlank(message = "集群名不能为空") @PathVariable("clusterName") String clusterName,
                                             @NotBlank(message = "Topic不能为空") @PathVariable("topic") String topic) {
        return ResponseEntity.success(topicService.detail(clusterName, topic));
    }

    @PostMapping
    public ResponseEntity<Empty> createTopic(@Valid @RequestBody CreateTopicRequest createTopicRequest) {
        topicService.createTopic(createTopicRequest);
        return ResponseEntity.success();
    }

    @GetMapping("/data/{clusterName}/{topic}")
    public ResponseEntity<List<KafkaData>> fetchMessageData(@NotBlank(message = "集群名不能为空") @PathVariable("clusterName") String clusterName,
                                                            @NotBlank(message = "Topic不能为空") @PathVariable("topic") String topic,
                                                            FetchDataRequest fetchDataRequest) {
        return ResponseEntity.success(topicService.fetchMessageData(clusterName, topic, fetchDataRequest));
    }

}
