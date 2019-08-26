package com.app.apptest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.aptannotation.AutoIntent;
import com.app.aptannotation.BindLayout;
import com.app.core.ViewBinding;

@BindLayout(R.layout.activity_hello)
public class HelloActivity extends AppCompatActivity {
    //    @AutoBundle
    //    int year;
    //    @AutoBundle
    //    boolean isAuto;
    //    @AutoBundle
    //    long time;
    //    @AutoBundle
    //    byte aByte;
    //    @AutoBundle
    //    short aShort;
    //    @AutoBundle
    //    char aChar;
    //    @AutoBundle
    //    float aFloat;
    //    @AutoBundle
    //    double aDouble;
    //    @AutoBundle
    //    String string;
    //
    //    @AutoBundle
    //    Integer integer1;
    //    @AutoBundle
    //    Boolean aBoolean1;
    //    @AutoBundle
    //    Long aLong1;
    //    @AutoBundle
    //    Byte aByte1;
    //    @AutoBundle
    //    Short aShort1;
    //    @AutoBundle
    //    Character character1;
    //    @AutoBundle
    //    Float aFloat1;
    //    @AutoBundle
    //    Double aDouble1;
    //
    //    @AutoBundle
    //    int[] ints;
    //    @AutoBundle
    //    boolean[] booleans;
    //    @AutoBundle
    //    long[] longs;
    //    @AutoBundle
    //    byte[] bytes;
    //    @AutoBundle
    //    short[] shorts;
    //    @AutoBundle
    //    char[] chars;
    //    @AutoBundle
    //    float[] floats;
    //    @AutoBundle
    //    double[] doubles;
    //    @AutoBundle
    //    String[] strings;
    //
    //    @AutoBundle
    //    ArrayList<Integer> listExtra;
    //    @AutoBundle
    //    CharSequence charSequenceExtra;
    //    @AutoBundle
    //    CharSequence[] charSequenceArrayExtra;
    //    @AutoBundle
    //    ArrayList<CharSequence> charSequenceArrayListExtra;
    //    @AutoBundle
    //    ArrayList<String> stringArrayListExtra;
    //
    //    @AutoBundle
    //    Bundle bundleExtra;
    //    @AutoBundle
    //    ParcelableBean parcelableExtra;
    //    @AutoBundle
    //    ParcelableBean[] parcelableBeans;
    //    @AutoBundle
    //    ArrayList<ParcelableBean> beanArrayList;
    //    @AutoBundle
    //    ClassBean[] classBeans;
    //    @AutoBundle
    //    ArrayList<ClassBean> classBeanArrayList;
    //    @AutoBundle
    //    SerializableBean serializableBean;
    //    @AutoBundle
    //    Size size;
    //    @AutoBundle
    //    ClassBinder binder;
    //    @AutoBundle
    //    SparseArray<ParcelableBean> sparseArray;

    @AutoIntent
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewBinding.bind(this);
//        HelloActivityViewBinding.bind(this);
        HelloActivityAutoIntent.bind(this);
    }

}
