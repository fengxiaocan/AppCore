package com.app.apptest;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Handler;
import android.os.MessageQueue;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.inputmethod.InputConnectionCompat;

import com.app.aptannotation.BindLayout;
import com.app.aptannotation.BindView;
import com.app.core.ViewBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

@BindLayout(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    @BindView(R.id.fl_container)
    FrameLayout container;

    private BottomNavigationView.OnNavigationItemSelectedListener
            mOnNavigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                replaceFragment("首页");
                return true;
            case R.id.navigation_dashboard:
                replaceFragment("导航");
                return true;
            case R.id.navigation_notifications:
                replaceFragment("通知");
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewBinding.bind(this);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        replaceFragment("首页");
    }

    private void replaceFragment(String title) {
        ClassBean bean = new ClassBean();
        bean.setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container,
                MainFragmentAutoBundle.build().title(bean).create()).commitNow();
    }
}
