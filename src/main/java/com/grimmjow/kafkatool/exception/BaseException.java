package com.grimmjow.kafkatool.exception;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public static void assertTrue(boolean condition, String message) {
        if (condition) {
            throw new BaseException(message);
        }
    }
}
