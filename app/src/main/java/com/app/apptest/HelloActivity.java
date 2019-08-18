package com.app.apptest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import com.app.aptannotation.AutoBundle;
import com.app.aptannotation.BindLayout;

import java.io.Serializable;
import java.util.ArrayList;

@BindLayout(R.layout.activity_hello)
public class HelloActivity extends AppCompatActivity {
    @AutoBundle
    int year;
    @AutoBundle
    boolean isAuto;
    @AutoBundle
    long time;
    @AutoBundle
    byte aByte;
    @AutoBundle
    short aShort;
    @AutoBundle
    char aChar;
    @AutoBundle
    float aFloat;
    @AutoBundle
    double aDouble;
    @AutoBundle
    String string;

    @AutoBundle
    Integer integer1;
    @AutoBundle
    Boolean aBoolean1;
    @AutoBundle
    Long aLong1;
    @AutoBundle
    Byte aByte1;
    @AutoBundle
    Short aShort1;
    @AutoBundle
    Character character1;
    @AutoBundle
    Float aFloat1;
    @AutoBundle
    Double aDouble1;

    @AutoBundle
    int[] ints;
    @AutoBundle
    boolean[] booleans;
    @AutoBundle
    long[] longs;
    @AutoBundle
    byte[] bytes;
    @AutoBundle
    short[] shorts;
    @AutoBundle
    char[] chars;
    @AutoBundle
    float[] floats;
    @AutoBundle
    double[] doubles;
    @AutoBundle
    String[] strings;

    @AutoBundle
    ArrayList<Integer> listExtra;
    @AutoBundle
    CharSequence charSequenceExtra;
    @AutoBundle
    CharSequence[] charSequenceArrayExtra;
    @AutoBundle
    ArrayList<CharSequence> charSequenceArrayListExtra;
    @AutoBundle
    ArrayList<String> stringArrayListExtra;

    @AutoBundle
    Bundle bundleExtra;
    @AutoBundle
    ParcelableBean parcelableExtra;
//    @AutoBundle
//    Parcelable[] parcelableArrayExtra;
//    @AutoBundle
//    ArrayList<Parcelable> parcelableArrayListExtra;
//    @AutoBundle
//    Serializable serializableExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelloActivityViewBinding.bind(this);
//        HelloActivityAutoBundle.bind(this);
        String name = "";
        Intent intent = getIntent();

        if (parcelableExtra instanceof Serializable){

        }
//        if (intent.hasExtra(name)) {
//            intent.getBooleanExtra(name, false);
//            intent.getByteExtra(name, (byte) 0);
//            intent.getShortExtra(name, (short) 0);
//            intent.getIntExtra(name, 0);
//            intent.getLongExtra(name, 0L);
//            intent.getCharExtra(name, '\u0000');
//            intent.getFloatExtra(name, 0F);
//            intent.getDoubleExtra(name, 0D);
//            intent.getStringExtra(name);
//
//            intent.getBooleanArrayExtra(name);
//            intent.getByteArrayExtra(name);
//            intent.getShortArrayExtra(name);
//            intent.getIntArrayExtra(name);
//            intent.getLongArrayExtra(name);
//            intent.getCharArrayExtra(name);
//            intent.getFloatArrayExtra(name);
//            intent.getDoubleArrayExtra(name);
//            intent.getStringArrayExtra(name);
//
//        ArrayList<Integer> listExtra = intent.getIntegerArrayListExtra(name);
//        CharSequence charSequenceExtra = intent.getCharSequenceExtra(name);
//        CharSequence[] charSequenceArrayExtra = intent.getCharSequenceArrayExtra(name);
//        ArrayList<CharSequence> charSequenceArrayListExtra = intent.getCharSequenceArrayListExtra(name);
//        ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(name);
//
//        Bundle bundleExtra = intent.getBundleExtra(name);
//        Parcelable parcelableExtra = intent.getParcelableExtra(name);
//        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra(name);
//        ArrayList<Parcelable> parcelableArrayListExtra = intent.getParcelableArrayListExtra(name);
//        Serializable serializableExtra = intent.getSerializableExtra(name);
//        }
    }

}
