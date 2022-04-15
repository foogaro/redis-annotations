package com.foogaro.data.redisframework.handlers.hash;

import com.foogaro.data.redisframework.handlers.DataStoreInvocationHandler;
import com.foogaro.data.redisframework.model.HASHCommands;
import com.foogaro.data.redisframework.model.JSONCommands;
import com.foogaro.data.redisframework.model.JSONSerializer;

import java.lang.reflect.Field;
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
            String id = calculateId(payload);
            Object redisResult = pushRedisCommands(prepareRedisCommands(id, payload).toArray());
            return transform(redisResult);
        } else {
            logger.error("Payload '{}' not valid for method '{}'.", args, method.getName());
            throw new IllegalArgumentException("Payload not valid.");
        }
    }

    @Override
    protected List<String> prepareRedisCommands(Object... parameters) {
        List<String> commands = new ArrayList<>();
        commands.add(HASHCommands.HSET.toString());
        commands.add((String) parameters[0]);
        commands.addAll(getFieldsValues(parameters[1]));
        return commands;
    }

    private List<String> getFieldsValues(Object payload) {
        List<String> fieldsValues = new ArrayList<>();
        try {
            String modelClassName = payload.getClass().getTypeName();
            Class<?> cls = Class.forName(modelClassName);
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                boolean canAccess = field.canAccess(payload);
                field.setAccessible(true);
                Object fieldValue = field.get(payload);
                field.setAccessible(canAccess);
                if (fieldValue != null) {
                    fieldsValues.add(field.getName());
                    fieldsValues.add(fieldValue.toString());
                }
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            if (logger.isTraceEnabled()) e.printStackTrace();
            logger.error("Error: {}", e.getMessage());
        }
        return fieldsValues;
    }

    @Override
    protected Long transform(Object result) {
        if (result != null && result instanceof Long) return (Long)result;
        return -1l;
    }

}
