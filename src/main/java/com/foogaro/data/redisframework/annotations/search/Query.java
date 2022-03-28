package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Query {
    String value();
}
