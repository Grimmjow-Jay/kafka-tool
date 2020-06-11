package com.grimmjow.kafkaTool.exception;

import java.util.Collection;
import java.util.Map;

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

    public static void assertNull(Object obj, String message) {
        if (obj == null) {
            throw new BaseException(message);
        }
    }

    public static void assertCondition(boolean condition, String message) {
        if (condition) {
            throw new BaseException(message);
        }
    }

    public static void assertBlank(CharSequence cs, String message) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            throw new BaseException(message);
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return;
            }
        }
        throw new BaseException(message);
    }

    public static void assertEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BaseException(message);
        }
    }

    public static void assertEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new BaseException(message);
        }
    }
}
