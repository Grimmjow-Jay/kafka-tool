package com.grimmjow.kafkatool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Grimm
 * @date 2020/8/28
 */
@RestController
public class ScheduleTestController {

    private ScheduledFuture<?> schedule;

    private final TaskScheduler taskScheduler;

    public ScheduleTestController(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @RequestMapping("scheduleTest")
    public String scheduleTest() {
        Runnable task = () -> System.out.println("====");
        schedule = taskScheduler.schedule(task, new CronTrigger("*/5 */1 * * * ?"));
        return "OK";
    }

    @RequestMapping("cancelTest")
    public String cancelTest() {
        schedule.cancel(true);
        return "OK";
    }

}
