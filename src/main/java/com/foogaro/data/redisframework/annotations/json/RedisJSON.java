package com.foogaro.data.redisframework.annotations.json;

import com.foogaro.data.redisframework.annotations.RFW;
import com.foogaro.data.redisframework.annotations.search.Index;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface RedisJSON {

    Index index();
    
}
