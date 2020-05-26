package com.grimmjow.kafkatool.config;

import org.springframework.util.StringUtils;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class ConstantConfig {

    public static final String DATA_PATH;

    static {
        String dataDirEnv = System.getProperty("data.dir");
        DATA_PATH = StringUtils.isEmpty(dataDirEnv) ? System.getProperty("java.io.tmpdir") : dataDirEnv;
    }
}
