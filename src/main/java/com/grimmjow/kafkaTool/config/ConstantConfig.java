package com.grimmjow.kafkaTool.config;

import java.util.concurrent.TimeUnit;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class ConstantConfig {

    public static final String DATA_PATH = System.getProperty("data.dir", System.getProperty("java.io.tmpdir"));

    public static final long DEFAULT_TIME_OUT = 10000L;

    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

}
