package com.foogaro.data.redisframework.factories;

import com.foogaro.data.redisframework.model.DataStoreUtil;
import com.foogaro.data.redisframework.model.DataType;

import java.lang.reflect.InvocationHandler;

import static com.foogaro.data.redisframework.model.DataType.valueOf;

public class ReadInvocationHandlerFactory {

    public static InvocationHandler get(String dataStoreOperationName, String dataStoreCommand) throws Throwable {
        DataType dataType = valueOf(DataStoreUtil.getAnnotationName(dataStoreOperationName));
        switch (dataType) {
            case JSON:
                return new com.foogaro.data.redisframework.handlers.json.ReadInvocationHandler();
            case HASH:
                return new com.foogaro.data.redisframework.handlers.hash.ReadInvocationHandler();
            default:
                throw new IllegalStateException("Invalid DataType model: " + dataType);
        }
    }

}
