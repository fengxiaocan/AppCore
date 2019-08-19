package com.app.apptest;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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

@BindLayout(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton fabAction;

    @BindView(R.id.tv_hello7)
    TextView hello7;

    @BindView(R.id.tv_hello8)
    TextView hello8;

    @BindView(R.id.tv_hello9)
    TextView hello9;

    @BindView(R.id.tv_hello10)
    TextView hello10;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MainActivityViewBinding.bind(this);
        setSupportActionBar(toolbar);

        hello7.setText("hello7");
        hello8.setText("hello8");
        hello9.setText("hello9");
        hello10.setText("hello10");
        boolean isImplementInterface = Parcelable.class.isAssignableFrom(ParcelableBean.class);
        Log.e("noah","isImplementInterface="+isImplementInterface);
    }

    @ViewClick(R.id.fab)
    void fabAction() {
        Snackbar.make(fabAction, "Replace with your own action", Snackbar.LENGTH_LONG).setAction(
                "Action", null).show();
    }

    @ViewClick({R.id.tv_hello, R.id.tv_hello1, R.id.tv_hello2, R.id.tv_hello3, R.id.tv_hello4,
            R.id.tv_hello5, R.id.tv_hello6, R.id.tv_hello7, R.id.tv_hello8, R.id.tv_hello9,
            R.id.tv_hello10})
    void helloAction(View view) {
//        HelloActivityAutoBundle.build()
//                .year(1)
//                .bundleExtra(new Bundle())
//                .charSequenceArrayExtra(new CharSequence[]{})
//                .start(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
