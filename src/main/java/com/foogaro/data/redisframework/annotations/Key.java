package com.foogaro.data.redisframework.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Key {
}
