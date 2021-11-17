package com.foogaro.data.redis.fts.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FTS
public @interface Numeric {

    boolean sortable() default false;

}
