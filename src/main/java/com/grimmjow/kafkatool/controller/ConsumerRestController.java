package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.request.EditOffsetRequest;
import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import com.grimmjow.kafkatool.service.ConsumerService;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    public ResponseEntity<List<String>> consumers(
            @NotBlank(message = "集群名不能为空") @PathVariable String clusterName) {
        return ResponseEntity.success(consumerService.consumers(clusterName));
    }

    @GetMapping("/offsets/{clusterName}/{consumerName}")
    public ResponseEntity<List<ConsumerTopicOffsetVo>> offsets(
            @NotBlank(message = "集群名不能为空") @PathVariable String clusterName,
            @NotBlank(message = "消费者名不能为空") @PathVariable String consumerName) {
        return ResponseEntity.success(consumerService.offsets(clusterName, consumerName));
    }

    @PutMapping("/offsets/{clusterName}/{consumerName}")
    public ResponseEntity<Empty> editOffset(@NotBlank(message = "集群名不能为空") @PathVariable String clusterName,
                                            @NotBlank(message = "消费者名不能为空") @PathVariable String consumerName,
                                            @Valid @RequestBody EditOffsetRequest editOffsetRequest) {
        consumerService.editOffset(clusterName, consumerName, editOffsetRequest);
        return ResponseEntity.success();
    }

}
