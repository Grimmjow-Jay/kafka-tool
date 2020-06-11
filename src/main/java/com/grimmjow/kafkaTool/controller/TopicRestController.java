package com.grimmjow.kafkaTool.controller;

import com.grimmjow.kafkaTool.entity.KafkaTopic;
import com.grimmjow.kafkaTool.entity.request.CreateTopicRequest;
import com.grimmjow.kafkaTool.entity.response.Empty;
import com.grimmjow.kafkaTool.entity.response.ResponseEntity;
import com.grimmjow.kafkaTool.service.TopicService;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Set<String>> topics(@PathVariable("clusterName") String clusterName) {
        return ResponseEntity.success(topicService.topics(clusterName));
    }

    @GetMapping("/detail/{clusterName}/{topic}")
    public ResponseEntity<KafkaTopic> detail(@PathVariable("clusterName") String clusterName,
                                             @PathVariable("topic") String topic) {
        return ResponseEntity.success(topicService.detail(clusterName, topic));
    }

    @PostMapping
    public ResponseEntity<Empty> createTopic(@RequestBody CreateTopicRequest createTopicRequest) {
        topicService.createTopic(createTopicRequest);
        return ResponseEntity.success();
    }

}
