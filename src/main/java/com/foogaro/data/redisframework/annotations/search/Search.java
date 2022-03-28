package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Search {

    /*
    2022-03-18
    https://oss.redis.com/redisearch/Commands/#ftsearch

FT.SEARCH {index} {query} [NOCONTENT] [VERBATIM] [NOSTOPWORDS] [WITHSCORES] [WITHPAYLOADS] [WITHSORTKEYS]
  [FILTER {numeric_attribute} {min} {max}] ...
  [GEOFILTER {geo_attribute} {lon} {lat} {radius} m|km|mi|ft]
  [INKEYS {num} {key} ... ]
  [INFIELDS {num} {attribute} ... ]
  [RETURN {num} {identifier} [AS {property}] ... ]
  [SUMMARIZE [FIELDS {num} {attribute} ... ] [FRAGS {num}] [LEN {fragsize}] [SEPARATOR {separator}]]
  [HIGHLIGHT [FIELDS {num} {attribute} ... ] [TAGS {open} {close}]]
  [SLOP {slop}] [INORDER]
  [LANGUAGE {language}]
  [EXPANDER {expander}]
  [SCORER {scorer}] [EXPLAINSCORE]
  [PAYLOAD {payload}]
  [SORTBY {attribute} [ASC|DESC]]
  [LIMIT offset num]
  [TIMEOUT {milliseconds}]
  [PARAMS {nargs} {name} {value} ... ]

     */

    Query query();
}
