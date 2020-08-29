package com.grimmjow.kafkatool.controller;

import com.grimmjow.kafkatool.entity.request.MonitorRequest;
import com.grimmjow.kafkatool.entity.response.Empty;
import com.grimmjow.kafkatool.entity.response.ResponseEntity;
import com.grimmjow.kafkatool.service.MonitorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    @RequestMapping("/enable")
    public ResponseEntity<Empty> enableMonitor(@RequestBody MonitorRequest monitorRequest) {
        monitorService.enableMonitor(monitorRequest);
        return ResponseEntity.success();
    }

    @PostMapping
    @RequestMapping("/disable")
    public ResponseEntity<Empty> disableMonitor(@RequestBody MonitorRequest monitorRequest) {
        monitorService.disableMonitor(monitorRequest);
        return ResponseEntity.success();
    }

}
