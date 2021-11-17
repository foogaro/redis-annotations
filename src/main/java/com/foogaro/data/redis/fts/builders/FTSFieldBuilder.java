package com.foogaro.data.redis.fts.builders;

import com.foogaro.data.redis.fts.model.FTSField;
import com.foogaro.data.redis.fts.model.FTSTypes;

public class FTSFieldBuilder {

    private FTSField field = new FTSField();

    private FTSFieldBuilder() {
    }

    public static FTSFieldBuilder builder() {
        return new FTSFieldBuilder();
    }

    public FTSFieldBuilder setName(String name) {
        this.field.setName(name);
        return this;
    }

    public FTSFieldBuilder setType(FTSTypes type) {
        this.field.setType(type);
        return this;
    }

    public FTSFieldBuilder setSortable(boolean sortable) {
        this.field.setSortable(sortable);
        return this;
    }

    public FTSFieldBuilder setCaseSensitive(boolean caseSensitive) {
        this.field.setCaseSensitive(caseSensitive);
        return this;
    }

    public FTSFieldBuilder setSeparator(String separator) {
        this.field.setSeparator(separator);
        return this;
    }

    public FTSField build() {
        return this.field;
    }
}
