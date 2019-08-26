package com.app.apptest;

import android.app.Instrumentation;
import android.app.UiAutomation;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.aptannotation.AutoBundle;
import com.app.aptannotation.BindLayout;
import com.app.aptannotation.BindView;
import com.app.core.ViewBinding;

@BindLayout(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.et_input)
    EditText etInput;

    @AutoBundle
    ClassBean title;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        return ViewBinding.inject(this, container);
        //        return inflater.inflate(R.layout.fragment_main,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainFragmentAutoBundle.bind(this);

        tvMessage.setText(title.getTitle());
    }
}
