package com.foogaro.data.redisframework.handlers.hash;

import com.foogaro.data.redisframework.handlers.DataStoreInvocationHandler;

import java.lang.reflect.Method;
import java.util.List;

public class ReadInvocationHandler extends DataStoreInvocationHandler {

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<String> prepareRedisCommands(Object... parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object transform(Object redisResult) {
        throw new UnsupportedOperationException();
    }

}
