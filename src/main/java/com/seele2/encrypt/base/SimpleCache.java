package com.seele2.encrypt.base;

import java.util.*;

public final class SimpleCache<K, V> {

    private final Map<K, V> pool = new HashMap<>();

    public boolean isPresent(K v) {
        return pool.containsKey(v);
    }

    public void put(K alias, V desensitizeEnum) {
        pool.put(alias, desensitizeEnum);
    }

    public V getValue(K k) {
        return pool.get(k);
    }


}
