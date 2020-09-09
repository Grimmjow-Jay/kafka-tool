package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.request.MonitorDataRequest;
import com.grimmjow.kafkatool.domain.request.MonitorTaskRequest;
import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import com.grimmjow.kafkatool.service.MonitorService;
import com.grimmjow.kafkatool.vo.ConsumerTopicOffsetVo;
import com.grimmjow.kafkatool.vo.MonitorTaskVo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    @PostMapping("/add")
    public ResponseEntity<Empty> addMonitor(@Valid @RequestBody MonitorTaskRequest monitorTaskRequest) {
        monitorService.addMonitor(monitorTaskRequest);
        return ResponseEntity.success();
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Empty> removeMonitor(@NotNull(message = "监控任务ID不能为空") @PathVariable Long id) {
        monitorService.removeMonitor(id);
        return ResponseEntity.success();
    }

    @GetMapping("/list")
    public ResponseEntity<List<MonitorTaskVo>> listMonitorTask(MonitorTaskRequest monitorTaskRequest) {
        return ResponseEntity.success(monitorService.listMonitorTask(monitorTaskRequest));
    }

    @GetMapping("/offset-data")
    public ResponseEntity<List<ConsumerTopicOffsetVo>> offsetData(@Valid MonitorDataRequest monitorDataRequest) {
        return ResponseEntity.success(monitorService.offsetData(monitorDataRequest));
    }

    @PutMapping("/active/{id}")
    public ResponseEntity<Empty> activeMonitor(@NotNull(message = "监控任务ID不能为空") @PathVariable Long id) {
        monitorService.activeMonitor(id);
        return ResponseEntity.success();
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<Empty> disableMonitor(@NotNull(message = "监控任务ID不能为空") @PathVariable Long id) {
        monitorService.disableMonitor(id);
        return ResponseEntity.success();
    }

}
