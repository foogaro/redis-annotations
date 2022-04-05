package com.foogaro.data.redisframework.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONSerializer {

    private final Logger logger = LoggerFactory.getLogger(JSONSerializer.class);

    private static Map<String, JsonAdapter> adapters = new HashMap<>();

    private JsonAdapter adapter;

    public JSONSerializer(Class clazz) {
        if (logger.isDebugEnabled()) logger.debug("Instantiating JSONSerializer for class: {}", clazz.getCanonicalName());
        adapter = adapters.get(clazz.getCanonicalName());
        if (adapter == null) {
            if (logger.isDebugEnabled()) logger.debug("JsonAdapter not found, creating a new one.");
            adapter = new Moshi.Builder().build().adapter(clazz);
            adapters.put(clazz.getCanonicalName(), adapter);
        }
    }

    public String toJson(Object model) {
        return adapter.toJson(model);
    }

    public Object fromJson(String json) {
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
