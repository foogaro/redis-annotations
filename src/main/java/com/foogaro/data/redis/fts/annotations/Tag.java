package com.foogaro.data.redis.fts.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FTS
public @interface Tag {

    String separator() default ",";

    boolean caseSensitive() default false;

    boolean sortable() default false;

}
