package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.entity.KafkaTopic;
import com.grimmjow.kafkatool.entity.ResponseEntity;
import com.grimmjow.kafkatool.service.TopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@RestController
public class TopicRestController {

    private final TopicService topicService;

    public TopicRestController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/topics")
    public ResponseEntity<Set<String>> topics(@RequestParam("clusterName") String clusterName) {
        return ResponseEntity.success(topicService.topics(clusterName));
    }

    @GetMapping("/topic")
    public ResponseEntity<KafkaTopic> detail(@RequestParam("clusterName") String clusterName,
                                             @RequestParam("topic") String topic) {
        return ResponseEntity.success(topicService.detail(clusterName, topic));
    }

}
