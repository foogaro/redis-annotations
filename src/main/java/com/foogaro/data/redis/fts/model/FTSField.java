package com.foogaro.data.redis.fts.model;

public class FTSField {

    private String name;
    private FTSTypes type;
    private boolean sortable;
    private boolean caseSensitive;
    private String separator;

    public FTSField() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FTSTypes getType() {
        return type;
    }

    public void setType(FTSTypes type) {
        this.type = type;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public String toString() {
        return "FTSField{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", sortable=" + sortable +
                ", caseSensitive=" + caseSensitive +
                ", separator='" + separator + '\'' +
                '}';
    }
}
