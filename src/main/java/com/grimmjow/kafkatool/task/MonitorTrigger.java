package com.grimmjow.kafkatool.task;

/**
 * @author Grimm
 * @date 2020/8/30
 */
public interface MonitorTrigger {

    /**
     * 触发时间间隔
     *
     * @return 时间间隔
     */
    long getInterval();

}
