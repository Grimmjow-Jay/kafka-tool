package com.grimmjow.kafkaTool.controller;

import com.grimmjow.kafkaTool.entity.ConsumerTopicOffset;
import com.grimmjow.kafkaTool.entity.response.ResponseEntity;
import com.grimmjow.kafkaTool.service.ConsumerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/27
 */
@RestController
@RequestMapping("consumer")
public class ConsumerRestController {

    private final ConsumerService consumerService;

    public ConsumerRestController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @GetMapping("/consumers/{clusterName}")
    public ResponseEntity<List<String>> consumers(@PathVariable String clusterName) {
        return ResponseEntity.success(consumerService.consumers(clusterName));
    }

    @GetMapping("/offsets/{clusterName}/{consumerName}")
    public ResponseEntity<List<ConsumerTopicOffset>> offsets(@PathVariable String clusterName, @PathVariable String consumerName) {
        return ResponseEntity.success(consumerService.offsets(clusterName, consumerName));
    }

}
