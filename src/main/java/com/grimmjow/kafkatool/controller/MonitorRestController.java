package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.request.MonitorDataRequest;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import com.grimmjow.kafkatool.service.MonitorService;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Grimm
 * @date 2020/8/28
 */
@RestController
@RequestMapping("monitor")
public class MonitorRestController {

    private final MonitorService monitorService;

    public MonitorRestController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @PostMapping("/enable")
    public ResponseEntity<Empty> enableMonitor(@Valid @RequestBody MonitorTaskRequest monitorTaskRequest) {
        monitorService.enableMonitor(monitorTaskRequest);
        return ResponseEntity.success();
    }

    @PostMapping("/disable")
    public ResponseEntity<Empty> disableMonitor(@Valid @RequestBody MonitorTaskRequest monitorTaskRequest) {
        monitorService.disableMonitor(monitorTaskRequest);
        return ResponseEntity.success();
    }

    @GetMapping("/offset-data")
    public ResponseEntity<List<ConsumerTopicOffsetVo>> offsetData(@Valid MonitorDataRequest monitorDataRequest) {
        return ResponseEntity.success(monitorService.offsetData(monitorDataRequest));
    }

}
