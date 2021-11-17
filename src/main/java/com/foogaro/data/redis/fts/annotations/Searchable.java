package com.foogaro.data.redis.fts.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FTS
public @interface Searchable {

    @AliasFor("value")
    String index() default "";
    @AliasFor("index")
    String value() default "";

    String keyPrefix() default "";

    boolean dropIndex() default false;

}
