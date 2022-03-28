# Redis Annotations
Framework annotations to use in your code for RedisSearch and RedisJSON.
At the moment only Java is supported.

## Introduction
This framework aims to provide a level of abstraction to leverage Redis with RediSearch and RedisJSON, by using an approach with Java annotations.

## Maven dependency
```xml
<dependency>
    <groupId>com.foogaro.util</groupId>
    <artifactId>resp4j</artifactId>
    <version>0.3.16</version>
</dependency>
```


## POJOs
```java
@RedisJSON(index = @Index(name = "ix-actors", indexStrategy = FTSIndexStrategy.CREATE, prefix = "actors:"))
public class Actor implements KeyValueModel {

    private long id;
    @Text
    private String firstname;
    @Text
    private String lastname;
    @Numeric(sortable = true)
    private int yearOfBirth;

    // constructors, getters, setters and so on

}

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

## DAOs
```java
@DataStore
public interface ActorRepository {

    @Search(query = @Query("@firstname:({{firstname}})"))
    public List<Actor> byFirstname(String firstname);
    @Search(query = @Query("@lastname:({{lastname}})"))
    public List<Actor> byLastname(String lastname);
    @Search(query = @Query("@yearOfBirth:({{yearOfBirth}})"))
    public List<Actor> byYearOfBirth(int yearOfBirth);

}

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

## Spring Boot Configuration
```java
@Configuration
public class RedisConfig {

    @Bean
    public FTSDetector getFTSDetector() {
        return new FTSDetector("com.foogaro.data.example.models");
    }

    @Bean
    public ActorRepository actorRepository() {
        DataStoreInvocationHandler invocationHandler = new DataStoreInvocationHandler();
        return (ActorRepository) Proxy.newProxyInstance(
                DataStoreInvocationHandler.class.getClassLoader(),
                new Class[] { ActorRepository.class},
                invocationHandler);
    }

    @Bean
    public MovieRepository movieRepository() {
        DataStoreInvocationHandler invocationHandler = new DataStoreInvocationHandler();
        return (MovieRepository) Proxy.newProxyInstance(
                DataStoreInvocationHandler.class.getClassLoader(),
                new Class[] { MovieRepository.class},
                invocationHandler);
    }

}
```

## Spring Data Configuration
Coming soon.

## What it does?

Compile the code, run it... and see something like the following in your Redis logs:

```bash
redis      | 1:M 17 Nov 2021 15:40:18.670 * <module> Scanning index idx-actors in background
redis      | 1:M 17 Nov 2021 15:40:18.672 * <module> Scanning indexes in background: done (scanned=1)
redis      | 1:M 17 Nov 2021 15:40:18.674 * <module> Scanning index idx-movies in background
redis      | 1:M 17 Nov 2021 15:40:18.674 * <module> Scanning indexes in background: done (scanned=1)
```

and you are good to go!

## Have a try in your Redis CLI first

On any text fields:
```bash
FT.SEARCH idx-actors "Tom"
```

On the "firstname" field only:
```bash
FT.SEARCH idx-actors "@firstname:Tom"
```

On the "title" field only:
```bash
FT.SEARCH idx-movies "@title:man"
```

Ranges, numbers:
```bash
FT.SEARCH idx-movies "@year:[1990 2021]"
```

## More Examples coming soon.
