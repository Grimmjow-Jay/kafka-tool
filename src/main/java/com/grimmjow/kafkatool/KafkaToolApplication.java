package com.grimmjow.kafkatool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Grimm
 * @since 2020/5/26
 */
@SpringBootApplication
@EnableScheduling
public class KafkaToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaToolApplication.class, args);
    }

}
