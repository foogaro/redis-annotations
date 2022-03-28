package com.foogaro.data.redisframework.model;

import java.util.ArrayList;
import java.util.List;

public class FTSIndex {

    private String name;
    private String on;
    private int prefixCount;
    private String prefixKeys;
    private List<FTSField> ftsFields = new ArrayList<>();
    private FTSIndexStrategy indexStrategy;

    private FTSIndex(Builder builder) {
        this.name = builder.name;
        this.on = builder.on;
        this.prefixCount = builder.prefixCount;
        this.prefixKeys = builder.prefixKeys;
        this.ftsFields = builder.ftsFields;
        this.indexStrategy = builder.indexStrategy;
    }

    public String getName() {
        return name;
    }

    public String getOn() {
        return on;
    }

    public int getPrefixCount() {
        return prefixCount;
    }

    public String getPrefixKeys() {
        return prefixKeys;
    }

    public List<FTSField> getFtsFields() {
        return ftsFields;
    }

    public FTSIndexStrategy getIndexStrategy() {
        return indexStrategy;
    }

    public static class Builder {
        private String name;
        private String on;
        private int prefixCount;
        private String prefixKeys;
        private List<FTSField> ftsFields = new ArrayList<>();
        private FTSIndexStrategy indexStrategy;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder on(String on) {
            this.on = on;
            return this;
        }

        public Builder prefixCount(int prefixCount) {
            this.prefixCount = prefixCount;
            return this;
        }

        public Builder prefixKeys(String prefixKeys) {
            this.prefixKeys = prefixKeys;
            return this;
        }

        public Builder addFTSField(FTSField ftsField) {
            this.ftsFields.add(ftsField);
            return this;
        }
        public Builder addFTFields(List<FTSField> ftsFields) {
            this.ftsFields.clear();
            this.ftsFields.addAll(ftsFields);
            return this;
        }

        public Builder indexStrategy(FTSIndexStrategy indexStrategy) {
            this.indexStrategy = indexStrategy;
            return this;
        }

        public FTSIndex build() {
            return new FTSIndex(this);
        }
    }

    @Override
    public String toString() {
        return "FTSIndex{" +
                "name='" + name + '\'' +
                "on='" + on + '\'' +
                ", prefixCount='" + prefixCount + '\'' +
                ", prefixKeys='" + prefixKeys + '\'' +
                ", ftsFields=" + ftsFields +
                ", indexStrategy=" + indexStrategy +
                '}';
    }
}
