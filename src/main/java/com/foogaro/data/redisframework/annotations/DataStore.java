package com.foogaro.data.redisframework.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface DataStore {

}
