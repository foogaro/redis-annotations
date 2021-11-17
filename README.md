# redis-annotations
Annotations to use in your code for RedisSearch (RedisJSON coming soon).

## Annotate your DTO
```java
@Entity
@Searchable(index = "idx-actors", keyPrefix = "actors", dropIndex = false)
public class Actor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Text
    private String firstname;
    @Text
    private String lastname;
    @Numeric(sortable = true)
    private int yearOfBirth;

    // constructors, getters, setters and so on

}

@Entity
@Searchable(index = "idx-movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Text(caseSensitive = true, sortable = true)
    private String title;
    @Numeric(sortable = true)
    private int rating;
    @Numeric
    private int year;

    // constructors, getters, setters and so on
    
}

```

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
