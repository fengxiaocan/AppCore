package com.app.apptest;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableBean extends ClassBean implements Parcelable,Runnable {
    private int year;
    private String name;

    protected ParcelableBean(Parcel in) {
        year = in.readInt();
        name = in.readString();
    }

    public static final Creator<ParcelableBean> CREATOR = new Creator<ParcelableBean>() {
        @Override
        public ParcelableBean createFromParcel(Parcel in) {
            return new ParcelableBean(in);
        }

        @Override
        public ParcelableBean[] newArray(int size) {
            return new ParcelableBean[size];
        }
    };

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(year);
        parcel.writeString(name);
    }

    @Override
    public void run() {

    }
}
