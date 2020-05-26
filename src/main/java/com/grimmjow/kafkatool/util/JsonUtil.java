package com.grimmjow.kafkatool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grimmjow.kafkatool.exception.BaseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author Grimm
 * @since 2020/5/26
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String objToJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new BaseException(e);
        }
    }

    public static <T> T jsonToObj(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new BaseException(e);
        }
    }

    public static <T> Collection<T> jsonToCollection(String json, Class<T> clz) {

        JavaType javaType = getCollectionType(Collection.class, clz);
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new BaseException(e);
        }
    }

    public static <T> List<T> jsonToList(String json, Class<T> clz) {

        JavaType javaType = getCollectionType(List.class, clz);
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new BaseException(e);
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> T load(File file, Class<T> clz) {
        try {
            return OBJECT_MAPPER.readValue(file, clz);
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    public static <T> T load(InputStream inputStream, Class<T> clz) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, clz);
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    public static <T> List<T> loadToList(File file, Class<T> clz) {
        JavaType javaType = getCollectionType(List.class, clz);
        try {
            return OBJECT_MAPPER.readValue(file, javaType);
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    public static <T> List<T> loadToList(InputStream inputStream, Class<T> clz) {
        JavaType javaType = getCollectionType(List.class, clz);
        try {
            return OBJECT_MAPPER.readValue(inputStream, javaType);
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

}
