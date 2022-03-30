# Redis Annotations
Framework to use in your code for __RediSearch__ and __RedisJSON__.
At the moment only Java is supported.

## Introduction
This framework (a more humble definition would be skeleton) aims to provide a level of abstraction to leverage _RediSearch_ and _RedisJSON_ Redis's modules.<br/>
The concept is very easy.

Whenever you need to provide _Full-Text Search_ capabilities in Redis, you need to load/enable the _RediSearch_ module.<br/>
Whenever you need to manage _JSON_ documents in Redis, you need to load/enable the _RedisJSON_ module.<br/>
But those are just Redis server capabilities, not your application's capabilities.<br/>
Hence, __redis-annotations__.

## HOW-TO
Before we can start let's see what we should do!

### Maven
Add the following dependency in your ```pom.xml```:
```xml
<dependency>
    <groupId>com.foogaro.util</groupId>
    <artifactId>redis-annotations</artifactId>
    <version>0.2.7</version>
</dependency>
```
Now you can use it in your code.

### Code 
To search for a JSON document, we need to index that document.
The JSON document has a specific structure, so we need to decide what to index and how to manage the index.

#### POJO
Let's take for example a simple JSON document which describes a movie: 
```json
{
  "title" : "Rocky",
  "rating" : 5,
  "year" : 1976
}
```
The index needs to know which fields need to be indexed.
Assuming we want to index the entire document, so that we can search for any attribute in full-text search, we will need to define a POJO representing the Movie document, as follows:

```java
@RedisJSON(index = @Index(name = "ix-movies", indexStrategy = FTSIndexStrategy.CREATE, prefix = "movies:"))
public class Movie implements KeyValueModel {

    private long id;
    @Text(sortable = true)
    private String title;
    @Numeric(sortable = true)
    private int rating;
    @Numeric
    private int year;

    // constructors, getters, setters and so on

}
```

- __@RedisJOSN__ defines the Movie class as JSON document;
- __@Index__ defines the specification of the Redis Index that will be created:
  - _name_ sets the name of the index (i.e. "ix-movies");
  - _indexStrategy_ manages the creation/alteration of the index, and it can be one of the following:
    - _NONE_ - index will not be created, nor destroyed;
    - _CREATE_ - index will be created, if it does not exist yet (like in the example above);
    - _DROP_ - index will be dropped/deleted, nor created if it does not exist yet;
    - _DROP_CREATE_ - index will be first dropped and then created
    - _UPDATE_ - index will be updated with the new specification, if it already exists.
      - Not implemented yet. 
  - _prefix_ sets the prefix of the key(s) to consider for indexing (i.e. all the keys starting with "movies:").
- __@Text__ sets a textual index to be applied to the relative field declaration (like for "title" in the example above);
- __@Numeric__ sets a numerical index to be applied to the relative field declaration (like for "rating" and "year" in the example above).

Now we need a way to manage our data models persistency with Redis.

#### Persistency
There are few annotations to enable data persistency.
Assuming that we want to continue with our Movie data model, and the index specification we defined, we need to also define how to search for our Movie JSON document.<br/>
So:
- by Title
- by Rating
- by Year

And this can be achieved defining an interface with those three methods, as follows:
```java
@DataStore
public interface MovieRepository {

    @Search(query = @Query("@title:({{title}})"))
    public List<Movie> byTitle(String title);
    @Search(query = @Query("@rating:({{rating}})"))
    public List<Movie> byRating(long rating);
    @Search(query = @Query("@year:({{year}})"))
    public List<Movie> byYear(int year);

}
```

- __@DataStore__ defines the interface as a DataStore (i.e. DAO, Spring Repository, and so on);
- __@Search__ contains query specification for the relative method that will be executed once invoked;
- __@Query__ defines the specification of the query that will be pushed to the Redis server to retrieve/set the data model.

The last piece of our puzzle consists of making the DAO available in the current classloader, which can be done in many different ways, depending on your application style.<br/>
Anyway, here is how you would make the DAO available:

```java
return (MovieRepository) Proxy.newProxyInstance(
        DataStoreInvocationHandler.class.getClassLoader(),
        new Class[] { MovieRepository.class},
        new DataStoreInvocationHandler());
```

And you are done, you can start up your application.

## Bootleggers roll your tapes
When the application starts, the _FTSDetector_ scans for all data models marked as @RedisJSON and creates the specified indexes.<br/>
What you should see in the Redis server logs is something like the following:

```bash
9:M 30 Mar 2022 07:11:52.474 * <module> Scanning index ix-movies in background
9:M 30 Mar 2022 07:11:52.475 * <module> Scanning indexes in background: done (scanned=10)
```

You can now benefit from a __full-text serach engine__ in Redis with your application or directly via the __redis-cli__:

#### curl

```shell
foogaro@MBP-di-Luigi redis-annotations-example % curl -v http://localhost:8080/api/movies/by-title/Gulp
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> GET /api/movies/by-title/Gulp HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.77.0
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Wed, 30 Mar 2022 19:35:05 GMT
<
* Connection #0 to host localhost left intact
[{"id":"movies:2","title":"Gulp","rating":2,"year":2004},{"id":"movies:4","title":"Gulp","rating":4,"year":2002},{"id":"movies:1","title":"Gulp","rating":1,"year":2005},{"id":"movies:5","title":"Gulp","rating":5,"year":2001},{"id":"movies:3","title":"Gulp","rating":3,"year":2003}]%
foogaro@MBP-di-Luigi redis-annotations-example %
```

#### redis-cli
```shell
localhost:6379> FT.SEARCH ix-movies @title:(Gulp)
 1) (integer) 5
 2) "movies:2"
 3) 1) "$"
    2) "{\"title\":\"Gulp\",\"year\":2004,\"rating\":2}"
 4) "movies:4"
 5) 1) "$"
    2) "{\"title\":\"Gulp\",\"year\":2002,\"rating\":4}"
 6) "movies:1"
 7) 1) "$"
    2) "{\"title\":\"Gulp\",\"year\":2005,\"rating\":1}"
 8) "movies:5"
 9) 1) "$"
    2) "{\"title\":\"Gulp\",\"year\":2001,\"rating\":5}"
10) "movies:3"
11) 1) "$"
    2) "{\"title\":\"Gulp\",\"year\":2003,\"rating\":3}"
localhost:6379>
```

You can find the example application at the [redis-annotations-example](https://github.com/foogaro/redis-annotations-example) repository.
