package com.app.apptest;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SerializableBean  implements Serializable {
    private int year;
    private String name;

    public int getYear() {
        return year;
    }

    public SerializableBean setYear(int year) {
        this.year = year;
        return this;
    }

    public String getName() {
        return name;
    }

    public SerializableBean setName(String name) {
        this.name = name;
        return this;
    }
}
