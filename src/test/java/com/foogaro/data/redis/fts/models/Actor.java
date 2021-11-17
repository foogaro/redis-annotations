package com.foogaro.data.redis.fts.models;

import com.foogaro.data.redis.fts.annotations.Numeric;
import com.foogaro.data.redis.fts.annotations.Searchable;
import com.foogaro.data.redis.fts.annotations.Text;

import java.io.Serializable;

@Searchable(index = "idx-actors", keyPrefix = "idx-actors", dropIndex = true)
public class Actor implements Serializable {

    @Deprecated
    private long id;
    @Text(sortable = true, caseSensitive = true)
    private String firstname;
    @Text(caseSensitive = true)
    private String lastname;
    @Numeric(sortable = true)
    private int yearOfBirth;

    public Actor() {
    }

    public Actor(String firstname, String lastname, int yearOfBirth) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.yearOfBirth = yearOfBirth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", yearOfBirth=" + yearOfBirth +
                '}';
    }
}
