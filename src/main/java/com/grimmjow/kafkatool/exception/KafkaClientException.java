package com.grimmjow.kafkatool.exception;

/**
 * @author Grimm
 * @date 2020/9/1
 */
public class KafkaClientException extends Exception {

    public KafkaClientException() {
    }

    public KafkaClientException(String message) {
        super(message);
    }

    public KafkaClientException(Throwable cause) {
        super(cause);
    }

}
