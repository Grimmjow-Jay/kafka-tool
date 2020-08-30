package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.domain.request.MonitorRequest;
import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import com.grimmjow.kafkatool.service.MonitorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public ResponseEntity<Empty> enableMonitor(@Valid @RequestBody MonitorRequest monitorRequest) {
        monitorService.enableMonitor(monitorRequest);
        return ResponseEntity.success();
    }

    @PostMapping("/disable")
    public ResponseEntity<Empty> disableMonitor(@Valid @RequestBody MonitorRequest monitorRequest) {
        monitorService.disableMonitor(monitorRequest);
        return ResponseEntity.success();
    }

}
