package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;
import com.foogaro.data.redisframework.model.DoubleMetaphone;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Text {

    String as() default "";
    boolean nostem() default true;
    boolean noindex() default false;
    boolean sortable() default false;
    int weight() default 1;
    DoubleMetaphone phonetic() default DoubleMetaphone.DM_EN;

}
