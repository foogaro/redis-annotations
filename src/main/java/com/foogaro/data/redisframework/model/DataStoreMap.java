package com.foogaro.data.redisframework.model;

import java.util.concurrent.ConcurrentHashMap;

public final class DataStoreMap {

    private static ConcurrentHashMap<String, DataStoreInfo> dataStoreMap;

    private DataStoreMap() {
    }

    public static synchronized ConcurrentHashMap<String, DataStoreInfo> getDataStoreMap() {
        if (dataStoreMap == null) {
            dataStoreMap = new ConcurrentHashMap<>();
        }

        return dataStoreMap;
    }
}
