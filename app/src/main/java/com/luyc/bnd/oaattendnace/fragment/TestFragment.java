package com.luyc.bnd.oaattendnace.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luyc.bnd.oaattendnace.R;

/**
 * Created by admin on 2017/9/2.
 */

public class TestFragment extends BaseFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view==null){
            view = inflater.inflate(R.layout.fragment_test,container,false);

        }

        return view;



    }
}
