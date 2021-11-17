package com.foogaro.data.redis.fts.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FTS
public @interface Text {

    boolean caseSensitive() default false;

    boolean sortable() default false;

}
