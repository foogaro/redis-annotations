package com.foogaro.data.redis.fts.builders;

import com.foogaro.data.redis.fts.model.FTSField;
import com.foogaro.data.redis.fts.model.FTSIndex;

public class FTSIndexBuilder {

    private FTSIndex index = new FTSIndex();

    private FTSIndexBuilder() {
    }

    public static FTSIndexBuilder builder() {
        return new FTSIndexBuilder();
    }

    public FTSIndexBuilder setName(String name) {
        this.index.setName(name);
        return this;
    }

    public FTSIndexBuilder setPrefix(String prefix) {
        this.index.setPrefix(prefix);
        return this;
    }

    public FTSIndexBuilder addFTSField(FTSField field) {
        this.index.addFtsFields(field);
        return this;
    }

    public FTSIndexBuilder setDropIndex(boolean dropIndex) {
        this.index.setDropIndex(dropIndex);
        return this;
    }


    public FTSIndex build() {
        return this.index;
    }

}
