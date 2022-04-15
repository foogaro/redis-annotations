package com.foogaro.data.redisframework.handlers;

public interface DataStoreOperation<T> {

    public Object create(T payload);
    public <T> T read(String id);
    public <T> T read(String id, String jsonPath);
    public Object update(T payload);
    public Object delete(String id);

}
