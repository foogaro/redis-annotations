package com.foogaro.data.redisframework.model;

import java.util.concurrent.ConcurrentHashMap;

public class DataStoreUtil {

    private static ConcurrentHashMap<String, DataStoreInfo> dataStoreMap = DataStoreMap.getDataStoreMap();

    private DataStoreUtil() {
    }

    public static String getDataStore(String dataStoreOperationName) {
        return dataStoreMap.get(dataStoreOperationName).getDataStore();
    }

    public static String getDataModel(String dataStoreOperationName) {
        return dataStoreMap.get(dataStoreOperationName).getDataModel();
    }

    public static String getAnnotationName(String dataStoreOperationName) {
        return dataStoreMap.get(dataStoreOperationName).getAnnotationName();
    }

    public static String getIndexName(String dataStoreOperationName) {
        return dataStoreMap.get(dataStoreOperationName).getIndexName();
    }

    public static String getIndexStrategy(String dataStoreOperationName) {
        return dataStoreMap.get(dataStoreOperationName).getIndexStrategy();
    }

    public static String getPrefix(String dataStoreOperationName) {
        return dataStoreMap.get(dataStoreOperationName).getPrefix();
    }

}
