package com.foogaro.data.redisframework.handlers.hash;

import com.foogaro.data.redisframework.handlers.DataStoreInvocationHandler;
import com.foogaro.data.redisframework.model.FTSCommand;
import com.foogaro.data.redisframework.model.JSONCommands;
import com.foogaro.data.redisframework.model.JSONSerializer;
import com.foogaro.data.redisframework.model.KeyValueModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.foogaro.data.redisframework.model.FTSConst.DOLLAR;
import static com.foogaro.data.redisframework.model.FTSConst.DOT;

public class UpdateInvocationHandler extends DataStoreInvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<String> prepareRedisCommands(Object... parameters) {
        throw new UnsupportedOperationException();
    }

    protected Boolean transform(Object result) {
        throw new UnsupportedOperationException();
    }

}
