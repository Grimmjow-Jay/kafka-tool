package com.grimmjow.kafkatool.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Grimm
 * @date 2020/9/10
 */
public class Pair<K, V> implements Serializable {

    private K key;

    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (key != null ? key.hashCode() : 0);
        hash = 31 * hash + (value != null ? value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pair) {
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
        }
        return false;
    }
}