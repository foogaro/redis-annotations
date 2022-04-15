package com.foogaro.data.redisframework.model;

import java.util.StringJoiner;

public class DataStoreInfo {

    private String dataStore;
    private String dataModel;
    private String annotationName;
    private String indexName;
    private String indexStrategy;
    private String prefix;

    private DataStoreInfo(Builder builder) {
        this.dataStore = builder.dataStore;
        this.dataModel = builder.dataModel;
        this.annotationName = builder.annotationName;
        this.indexName = builder.indexName;
        this.indexStrategy = builder.indexStrategy;
        this.prefix = builder.prefix;
    }

    public String getDataStore() {
        return dataStore;
    }

    public String getDataModel() {
        return dataModel;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexStrategy() {
        return indexStrategy;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataStoreInfo.class.getSimpleName() + "[", "]")
                .add("dataStore='" + dataStore + "'")
                .add("dataModel='" + dataModel + "'")
                .add("annotationName='" + annotationName + "'")
                .add("indexName='" + indexName + "'")
                .add("indexStrategy='" + indexStrategy + "'")
                .add("prefix='" + prefix + "'")
                .toString();
    }

    public static class Builder {
        private String dataStore;
        private String dataModel;
        private String annotationName;
        private String indexName;
        private String indexStrategy;
        private String prefix;

        public Builder dataStore(String dataStore) {
            this.dataStore = dataStore;
            return this;
        }

        public Builder dataModel(String dataModel) {
            this.dataModel = dataModel;
            return this;
        }

        public Builder annotationName(String annotationName) {
            this.annotationName = annotationName;
            return this;
        }

        public Builder indexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public Builder indexStrategy(String indexStrategy) {
            this.indexStrategy = indexStrategy;
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public DataStoreInfo build() {
            return new DataStoreInfo(this);
        }
    }

}
