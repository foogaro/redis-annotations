package com.foogaro.data.redisframework.handlers;

import com.foogaro.data.redis.resp.Redis;
import com.foogaro.data.redis.resp.exceptions.RedisError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.List;

public abstract class DataStoreInvocationHandler implements InvocationHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract List<String> prepareRedisCommands(Object... parameters);

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
