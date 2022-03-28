package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;
import com.foogaro.data.redisframework.model.FTSConst;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Tag {

    boolean noindex() default false;
    String separator() default FTSConst.COMMA;
    String as() default "";
    boolean caseSensitive() default false;
    boolean sortable() default false;

}
