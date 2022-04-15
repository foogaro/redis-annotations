package com.foogaro.data.redisframework.model;

import java.io.Serializable;

public interface KeyValueModel extends Serializable {

    public void setKey(String key);
    public Object getKey();

}
