package com.foogaro.data.redis.fts.builders;

import com.foogaro.data.redis.fts.model.FTSIndex;

import java.util.ArrayList;
import java.util.List;

public class FTSBuilder {

    private List<FTSIndex> indices = new ArrayList<>();

    private FTSBuilder() {
    }

    public static FTSBuilder builder() {
        return new FTSBuilder();
    }

    public FTSBuilder addFTSIndex(FTSIndex index) {
        this.indices.add(index);
        return this;
    }

    public List<FTSIndex> build() {
        return this.indices;
    }

    @Override
    public String toString() {
        return "FTSBuilder{" +
                "indices=" + indices +
                '}';
    }
}
