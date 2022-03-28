package com.foogaro.data.redisframework.model;

public enum DoubleMetaphone {
    DM_EN("dm:en"),
    DM_FR("dm:fr"),
    DM_PT("dm:pt"),
    DM_ES("dm:es");

    public final String matcher;
    DoubleMetaphone(String matcher) {
        this.matcher = matcher;
    }
}
