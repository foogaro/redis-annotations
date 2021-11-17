package com.foogaro.data.redis.fts.model;

import java.util.ArrayList;
import java.util.List;

public class FTSIndex {

    private String name;
    private String prefix;
    private List<FTSField> ftsFields = new ArrayList<>();
    private boolean dropIndex;

    public FTSIndex() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<FTSField> getFtsFields() {
        return ftsFields;
    }

    public void addFtsFields(FTSField ftsField) {
        this.ftsFields.add(ftsField);
    }

    public boolean isDropIndex() {
        return dropIndex;
    }

    public void setDropIndex(boolean dropIndex) {
        this.dropIndex = dropIndex;
    }

    @Override
    public String toString() {
        return "FTSIndex{" +
                "name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", ftsFields=" + ftsFields +
                ", dropIndex=" + dropIndex +
                '}';
    }
}
