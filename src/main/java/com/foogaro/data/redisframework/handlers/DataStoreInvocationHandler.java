package com.foogaro.data.redisframework.handlers;

import com.foogaro.data.redis.resp.Redis;
import com.foogaro.data.redis.resp.exceptions.RedisError;
import com.foogaro.data.redisframework.annotations.json.RedisJSON;
import com.foogaro.data.redisframework.annotations.search.Aggregate;
import com.foogaro.data.redisframework.annotations.search.Query;
import com.foogaro.data.redisframework.annotations.search.Search;
import com.foogaro.data.redisframework.model.KeyValueModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.foogaro.data.redisframework.model.FTSCommand.AGGREGATE;
import static com.foogaro.data.redisframework.model.FTSCommand.SEARCH;
import static com.foogaro.data.redisframework.model.FTSConst.DOT;
import static com.foogaro.data.redisframework.model.FTSConst.FULL_TEXT_SEARCH;

public class DataStoreInvocationHandler implements InvocationHandler {

    private final Logger logger = LoggerFactory.getLogger(DataStoreInvocationHandler.class);

    private final String QUERY_PATTERN = "\\{\\{(.+?)\\}\\}";

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (logger.isDebugEnabled()) logger.debug("invoke.method: {}", method.getName());
        String indexName = retrieveIndexName(method.getGenericReturnType());
        if (logger.isDebugEnabled()) logger.debug("invoke.indexName: " + indexName);
        Type model = method.getGenericReturnType();
        if (logger.isDebugEnabled()) logger.debug("invoke.com.foogaro.data.redisframework.model: " + model);
        Annotation[] search = method.getDeclaredAnnotations();
        if (logger.isDebugEnabled()) logger.debug("invoke.search: " + search);
        if (search != null && search.length == 1) {
            List<String> commands = new ArrayList<>();
            Annotation annotation = search[0];
            if (logger.isDebugEnabled()) logger.debug("invoke.annotation: " + annotation);
            if (annotation != null) {
                Query query = null;
                if (annotation instanceof Search) {
                    commands.add(FULL_TEXT_SEARCH + DOT + SEARCH);
                    query = ((Search)annotation).query();
                } else if (annotation instanceof Aggregate) {
                    commands.add(FULL_TEXT_SEARCH + DOT + AGGREGATE);
                    query = ((Aggregate)annotation).query();
                }
                if (query != null) {
                    String resolvedQuery = findAndReplaceQueryValues(query.value(), args);
                    commands.add(indexName);
                    commands.add(resolvedQuery);
                    Object redisResult = pushRedisCommand(commands.stream().toArray());

                    return transform(redisResult, retrieveModelType(model));
                }
            }
        }
        throw new IllegalArgumentException("Invalid parameters.");
    }

/*
    1) (integer) 5
    2) "movies:1"
    3)  1) "$"
        2) "{\"title\":\"Gulp\",\"year\":2005,\"rating\":1}"
    4) "movies:2"
    5)  1) "$"
        2) "{\"title\":\"Gulp\",\"year\":2004,\"rating\":2}"
    6) "movies:3"
    7)  1) "$"
        2) "{\"title\":\"Gulp\",\"year\":2003,\"rating\":3}"
    8) "movies:4"
    9)  1) "$"
        2) "{\"title\":\"Gulp\",\"year\":2002,\"rating\":4}"
    10) "movies:5"
    11) 1) "$"
        2) "{\"title\":\"Gulp\",\"year\":2001,\"rating\":5}"
 */

    private Object transform(Object redisResult, String modelClassName) {
        List result = new ArrayList<>();
        List response = (List) redisResult;
        int size = response.size();
        for (int i=1; i<size; i++) {
            String id = new String((byte[])response.get(i++));
            List strings = (List) response.get(i);
            String dollar = new String((byte[])strings.get(0));
            String json = new String((byte[])strings.get(1));
            Gson gson = new GsonBuilder().create();
            Class<?> theClass = null;
            try {
                theClass = Class.forName(modelClassName);
                Object model = gson.fromJson(json, theClass);
                ((KeyValueModel)model).setId(id);
                result.add(theClass.cast(model));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private String retrieveIndexName(Type type) {
        ParameterizedType pt = (ParameterizedType)type;
        try {
            String obj = pt.getActualTypeArguments()[0].getTypeName();
            if (logger.isDebugEnabled()) logger.debug("retrieveIndexName.obj: " + obj);
            RedisJSON annotation = Class.forName(obj).getDeclaredAnnotation(RedisJSON.class);
            if (logger.isDebugEnabled()) logger.debug("retrieveIndexName.annotation: " + annotation);
            return annotation.index().name();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private String retrieveModelType(Type type) {
        ParameterizedType pt = (ParameterizedType)type;
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length == 1)
        return ((Class)actualTypeArguments[0]).getName();
        return type.getTypeName();
    }

    private Object pushRedisCommand(Object[] commands) {
        Object redisCall = null;
        try {
            redisCall = new Redis().call(commands);
        } catch (RedisError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return redisCall;
    }

    private String findAndReplaceQueryValues(String queryValue, Object[] args) {
        Pattern p = Pattern.compile(QUERY_PATTERN, Pattern.DOTALL);
        Matcher m = p.matcher(queryValue);
        int argsIndex = 0;
        StringBuffer resolvedQueryValue = new StringBuffer();
        while (m.find()) {
            String match = m.group();
            resolvedQueryValue.append(replaceParameterValues(queryValue, match, args[argsIndex++]));
        }
        return resolvedQueryValue.toString();
    }
    private String replaceParameterValues(String bulk, String param, Object value) {
        if (value != null) {
            if (value instanceof String) {
                return bulk.replace(param, (String) value);
            } else if (value instanceof Integer) {
                return bulk.replace(param, ((Integer) value).toString());
            } else if (value instanceof Long) {
                return bulk.replace(param, ((Long) value).toString());
            }
        }
        throw new IllegalArgumentException("Invalid parameter value");
    }
}
