package com.luyc.bnd.oaattendnace.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.utils.StatusBarCompat;

public abstract class MyBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_base);
        StatusBarCompat.compat(this,R.color.white);
    }
}
