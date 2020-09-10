package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.KafkaTopic;
import com.grimmjow.kafkatool.domain.request.CreateTopicRequest;
import com.grimmjow.kafkatool.domain.request.FetchMessageRequest;
import com.grimmjow.kafkatool.domain.request.ProduceMessageRequest;
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

    @GetMapping("/message/{clusterName}/{topic}")
    public ResponseEntity<List<KafkaData<String, String>>> fetchMessage(
            @NotBlank(message = "集群名不能为空") @PathVariable("clusterName") String clusterName,
            @NotBlank(message = "Topic不能为空") @PathVariable("topic") String topic,
            FetchMessageRequest fetchMessageRequest) {
        fetchMessageRequest = fetchMessageRequest == null ? new FetchMessageRequest() : fetchMessageRequest;
        return ResponseEntity.success(topicService.fetchMessage(clusterName, topic, fetchMessageRequest));
    }

    @PostMapping("/message/{clusterName}/{topic}")
    public ResponseEntity<Empty> produce(@NotBlank(message = "集群名不能为空") @PathVariable("clusterName") String clusterName,
                                         @NotBlank(message = "Topic不能为空") @PathVariable("topic") String topic,
                                         @Valid @RequestBody ProduceMessageRequest request) {
        topicService.produce(clusterName, topic, request.getKey(), request.getMessage());
        return ResponseEntity.success();
    }

}
