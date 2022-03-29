package com.foogaro.data.redisframework.handlers;

public interface DataStoreOperations<T> {

    public boolean create(T payload);
    public T read(Object id);
    public int update(T payload);
    public int delete(Object id);

}
