package com.foogaro.data.redisframework.handlers.json;

import com.foogaro.data.redisframework.handlers.DataStoreInvocationHandler;
import com.foogaro.data.redisframework.model.JSONCommands;
import com.foogaro.data.redisframework.model.JSONSerializer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.foogaro.data.redisframework.model.FTSCommand.JSON;
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
            //Object id = ((KeyValueModel)payload).getId();
            String id = calculateId(payload);
            Object redisResult = pushRedisCommands(prepareRedisCommands(id, json).toArray());
            return transform(redisResult);
        } else {
            logger.error("Payload '{}' not valid for method '{}'.", args, method.getName());
            throw new IllegalArgumentException("Payload nxot valid.");
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

    protected Boolean transform(Object result) {
        byte[] bytes = (byte[]) result;
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char)b);
        }
        return OK.equalsIgnoreCase(sb.toString());
    }

}
