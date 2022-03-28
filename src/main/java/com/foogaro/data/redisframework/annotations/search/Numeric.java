package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Numeric {

    String as() default "";
    boolean sortable() default false;

}
