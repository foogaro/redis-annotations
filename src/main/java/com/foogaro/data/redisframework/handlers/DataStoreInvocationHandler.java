package com.foogaro.data.redisframework.handlers;

import com.foogaro.data.redis.resp.Redis;
import com.foogaro.data.redis.resp.exceptions.RedisError;
import com.foogaro.data.redisframework.annotations.Key;
import com.foogaro.data.redisframework.model.DataStoreUtil;
import com.foogaro.data.redisframework.model.KeyValueModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import static com.foogaro.data.redisframework.model.FTSConst.COLON;

public abstract class DataStoreInvocationHandler implements InvocationHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract List<String> prepareRedisCommands(Object... parameters);
    protected abstract Object transform(Object redisResult);

    protected String calculateId(Object payload) {
        String result = payload.getClass().getSimpleName().toLowerCase() + COLON + ((KeyValueModel)payload).getKey();
        try {
            String modelClassName = payload.getClass().getTypeName();
            Class<?> cls = Class.forName(modelClassName);
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                Annotation ann = field.getAnnotation(Key.class);
                if (ann != null && ann instanceof Key) {
                    Key aKey = (Key)ann;
                    String prefix = normalizePrefix(DataStoreUtil.getPrefix(modelClassName), payload.getClass().getSimpleName().toLowerCase());
                    boolean canAccess = field.canAccess(payload);
                    field.setAccessible(true);
                    Object fieldValue = field.get(payload);
                    field.setAccessible(canAccess);
                    result = prefix + fieldValue;
                    break;
                }
                logger.warn("There is no filed annotated as @Key.");
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            if (logger.isTraceEnabled()) e.printStackTrace();
            logger.error("Error: {}", e.getMessage());
        }
        logger.debug("Calculated ID: {}", result);
        return result;
    }

    private String normalizePrefix(String prefix, String defaultPrefix) {
        System.out.println("PREFIX: " + prefix);
        String result = prefix;
        System.out.println("result: " + result);
        if (result == null || "".equalsIgnoreCase(result.trim())) result = defaultPrefix;
        System.out.println("result: " + result);
        if (result.lastIndexOf(COLON) != (result.length()-1)) result = result + COLON;
        System.out.println("result: " + result);
        return result;
    }

    protected Object pushRedisCommands(Object[] commands) {
        Object redisCall = null;
        if (logger.isDebugEnabled()) Arrays.stream(commands).forEach(o -> logger.debug("Command: {}", o));
        try {
            redisCall = new Redis().call(commands);
        } catch (RedisError e) {
            e.printStackTrace();
            logger.error("RedisError: {}", e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("RedisException: {}", e);
        }
        return redisCall;
    }

}
