package com.mehedi.pchtest.db;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
//@android.support.annotation.NonNull

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String id;


    private String firstName;

    private String lastName;

    private String picture;

    private int dailyWage;

    private int numberOfDays;

    public User(String id, String firstName, String lastName, String picture, int dailyWage, int numberOfDays) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.picture = picture;
        this.dailyWage = dailyWage;
        this.numberOfDays = numberOfDays;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPicture() {
        return picture;
    }

    public int getDailyWage() {
        return dailyWage;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }
}
