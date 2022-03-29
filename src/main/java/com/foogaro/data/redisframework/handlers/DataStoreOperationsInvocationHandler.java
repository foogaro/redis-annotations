package com.foogaro.data.redisframework.handlers;

import com.foogaro.data.redisframework.model.DataStoreCommands;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DataStoreOperationsInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (DataStoreCommands.CREATE.toString().equalsIgnoreCase(methodName)) {
            return new CreateInvocationHandler().invoke(proxy, method, args);
        } else if (DataStoreCommands.READ.toString().equalsIgnoreCase(methodName)) {
            return new ReadInvocationHandler().invoke(proxy, method, args);
        } else if (DataStoreCommands.UPDATE.toString().equalsIgnoreCase(methodName)) {
            return new UpdateInvocationHandler().invoke(proxy, method, args);
        } else if (DataStoreCommands.DELETE.toString().equalsIgnoreCase(methodName)) {
            return new DeleteInvocationHandler().invoke(proxy, method, args);
        } else {
            return new QueryInvocationHandler().invoke(proxy, method, args);
        }
    }
    
}
