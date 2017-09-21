package com.luyc.bnd.oaattendnace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RetroactiveDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.sp_name)
    Spinner spName;
    @InjectView(R.id.tv_type)
    TextView tvType;
    @InjectView(R.id.sp_type)
    Spinner spType;
    @InjectView(R.id.tv_how_time)
    TextView tvHowTime;
    @InjectView(R.id.et_how_time)
    EditText etHowTime;
    @InjectView(R.id.rb_discommit)
    RadioButton rbDiscommit;
    @InjectView(R.id.rb_commit)
    RadioButton rbCommit;
    @InjectView(R.id.activity_retroactive_details)
    LinearLayout activityRetroactiveDetails;
    private ArrayAdapter<String> spNameAdapter=null,typeAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retroactive_details);
        ButterKnife.inject(this);
        initMenuData();
    }

    private void initMenuData() {
        if (typeAdapter==null){
            typeAdapter = new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.ReleaseType_iii));
        }
        typeAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        if (spNameAdapter==null){
            spNameAdapter = new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.ReleaseName));
        }
        spNameAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spName.setAdapter(spNameAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RetroactiveDetailsActivity.this,"您选择了"+str,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RetroactiveDetailsActivity.this,"您选择了"+str,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @OnClick({R.id.iv_back, R.id.rb_discommit, R.id.rb_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rb_discommit:
                finish();
                break;
            case R.id.rb_commit:
                break;
        }
    }
}
