package com.app.apptest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.aptannotation.AutoBundle;
import com.app.aptannotation.BindView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {

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

//    @AutoBundle
//    int aid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainFragmentViewBinding binding = MainFragmentViewBinding.bind(this, view);
    }
}
