package com.foogaro.data.redisframework.handlers;

import com.foogaro.data.redisframework.model.JSONCommands;
import com.foogaro.data.redisframework.model.JSONSerializer;
import com.foogaro.data.redisframework.model.KeyValueModel;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.foogaro.data.redisframework.model.FTSConst.*;

public class CreateInvocationHandler extends DataStoreInvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args != null && args.length == 1) {
            Object payload = args[0];
            String modelClassName = payload.getClass().getTypeName();
            Class<?> cls = Class.forName(modelClassName);
            String json = new JSONSerializer(cls).toJson(payload);
            if (logger.isDebugEnabled()) logger.debug("JSON for type {}: {}", modelClassName, json);
            Object id = ((KeyValueModel)payload).getId();
            Object redisResult = pushRedisCommands(prepareRedisCommands(id, json).toArray());
            return transform(redisResult);
        } else {
            logger.error("Payload '{}' not valid for method '{}'.", args, method.getName());
            throw new IllegalArgumentException("Payload not valid.");
        }
    }

    @Override
    protected List<String> prepareRedisCommands(Object... parameters) {
        List<String> commands = new ArrayList<>();
        commands.add(JSON + DOT + JSONCommands.SET);
        commands.add((String) parameters[0]);
        commands.add(DOLLAR);
        commands.add((String) parameters[1]);
        return commands;
    }

    private Boolean transform(Object result) {
        byte[] bytes = (byte[]) result;
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char)b);
        }
        return OK.equalsIgnoreCase(sb.toString());
    }

}
