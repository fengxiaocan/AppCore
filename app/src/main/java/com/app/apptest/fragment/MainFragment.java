package com.app.apptest.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.apptest.R;
import com.app.apptest.data.ClassBean;
import com.app.aptannotation.AutoBundle;
import com.app.aptannotation.BindLayout;
import com.app.aptannotation.BindView;

@BindLayout(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @BindView(R.id.et_input)
    EditText etInput;

    @BindView(R.id.tv_message)
    TextView tvMessage;

    @AutoBundle
    ClassBean title;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainFragmentAutoBundle.bind(this);
        tvMessage.setText(title.getTitle());
    }

}
