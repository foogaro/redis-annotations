package com.foogaro.data.redisframework.model;

public class FTSField {

    private String name;
    private String asName;
    private FTSTypes type;
    private boolean sortable;
    private boolean caseSensitive;
    private String separator;

    private FTSField(Builder builder) {
        this.name = builder.name;
        this.asName = builder.asName;
        this.type = builder.type;
        this.sortable = builder.sortable;
        this.caseSensitive = builder.caseSensitive;
        this.separator = builder.separator;
    }

    public String getName() {
        return name;
    }
    public String getAsName() {
        return asName;
    }

    public FTSTypes getType() {
        return type;
    }

    public boolean isSortable() {
        return sortable;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public String getSeparator() {
        return separator;
    }

    public static class Builder {
        private String name;
        private String asName;
        private FTSTypes type;
        private boolean sortable;
        private boolean caseSensitive;
        private String separator;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder asName(String asName) {
            this.asName = asName;
            return this;
        }

        public Builder type(FTSTypes type) {
            this.type = type;
            return this;
        }

        public Builder sortable(Boolean sortable) {
            this.sortable = sortable;
            return this;
        }

        public Builder caseSensitive(Boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public Builder separator(String separator) {
            this.separator = separator;
            return this;
        }

        public FTSField build() {
            return new FTSField(this);
        }

    }

    @Override
    public String toString() {
        return "FTSField{" +
                "name='" + name + '\'' +
                ", asName='" + asName + '\'' +
                ", type=" + type +
                ", sortable=" + sortable +
                ", caseSensitive=" + caseSensitive +
                ", separator='" + separator + '\'' +
                '}';
    }
}
