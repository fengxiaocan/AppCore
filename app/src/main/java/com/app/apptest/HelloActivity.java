package com.app.apptest;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.aptannotation.BindLayout;
import com.app.aptannotation.BindView;
import com.app.aptannotation.ViewClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

@BindLayout(R.layout.activity_hello)
public class HelloActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        HelloActivityViewBinding.bind(this);
    }

}
