package com.seele2.encrypt.tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SimpleCache<K, V> {

    private final Map<K, V> pool = new ConcurrentHashMap<>();

    public boolean isPresent(K v) {
        return pool.containsKey(v);
    }

    public void put(K k, V v) {
        pool.put(k, v);
    }

    public V getValue(K k) {
        return pool.get(k);
    }

    public boolean empty() {
        return pool.isEmpty();
    }


}
