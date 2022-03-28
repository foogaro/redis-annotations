package com.foogaro.data.redisframework.annotations.search;

import com.foogaro.data.redisframework.annotations.RFW;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RFW
public @interface Aggregate {

    /*
    2022-03-18
    https://oss.redis.com/redisearch/Commands/#ftaggregate

FT.AGGREGATE {index_name}
  {query_string}
  [VERBATIM]
  [LOAD {nargs} {identifier} [AS {property}] ...]
  [GROUPBY {nargs} {property} ...
    REDUCE {func} {nargs} {arg} ... [AS {name:string}]
    ...
  ] ...
  [SORTBY {nargs} {property} [ASC|DESC] ... [MAX {num}]]
  [APPLY {expr} AS {alias}] ...
  [LIMIT {offset} {num}] ...
  [FILTER {expr}] ...
  [TIMEOUT {milliseconds}]

     */

    Query query();

}
