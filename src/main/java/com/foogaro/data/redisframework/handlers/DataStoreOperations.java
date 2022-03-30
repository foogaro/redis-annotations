package com.foogaro.data.redisframework.handlers;

public interface DataStoreOperations<T> {

    public boolean create(T payload);
    public <T> T read(String id, String jsonPath);
    public int update(T payload);
    public int delete(String id);

}
