package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;
import com.foogaro.data.redisframework.model.FTSIndexStrategy;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Index {

    /*
    2022-03-18
    https://oss.redis.com/redisearch/Commands/#ftcreate

  FT.CREATE {index}
    [ON {data_type}]
       [PREFIX {count} {prefix} [{prefix} ...]
       [FILTER {filter}]
       [LANGUAGE {default_lang}]
       [LANGUAGE_FIELD {lang_attribute}]
       [SCORE {default_score}]
       [SCORE_FIELD {score_attribute}]
       [PAYLOAD_FIELD {payload_attribute}]
    [MAXTEXTFIELDS] [TEMPORARY {seconds}] [NOOFFSETS] [NOHL] [NOFIELDS] [NOFREQS] [SKIPINITIALSCAN]
    [STOPWORDS {num} {stopword} ...]
    SCHEMA {identifier} [AS {attribute}]
        [TEXT [NOSTEM] [WEIGHT {weight}] [PHONETIC {matcher}] | NUMERIC | GEO | TAG [SEPARATOR {sep}] [CASESENSITIVE] [SORTABLE [UNF]] [NOINDEX]] |
        [VECTOR {algorithm} {count} [{attribute_name} {attribute_value} ...]] ...
     */

    String name() default "";
    String[] prefix() default {};
    FTSIndexStrategy indexStrategy() default FTSIndexStrategy.NONE;

}
