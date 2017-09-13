package com.luyc.bnd.oaattendnace.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luyc.bnd.oaattendnace.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PunchDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.iv_attend)
    ImageView ivAttend;
    @InjectView(R.id.tv_attendance_time_i)
    TextView tvAttendanceTimeI;
    @InjectView(R.id.tv_company_work_time)
    TextView tvCompanyWorkTime;
    @InjectView(R.id.tv_addressed)
    TextView tvAddressed;
    @InjectView(R.id.tv_work_nor_i)
    TextView tvWorkNorI;
    @InjectView(R.id.tv_updata_attend_i)
    TextView tvUpdataAttendI;
    @InjectView(R.id.iv_img_i)
    ImageView ivImgI;
    @InjectView(R.id.iv_attend_i)
    ImageView ivAttendI;
    @InjectView(R.id.tv_attendance_time_ii)
    TextView tvAttendanceTimeIi;
    @InjectView(R.id.textView5)
    TextView textView5;
    @InjectView(R.id.tv_addressed_i)
    TextView tvAddressedI;
    @InjectView(R.id.tv_work_nor_ii)
    TextView tvWorkNorIi;
    @InjectView(R.id.tv_updata_attend_ii)
    TextView tvUpdataAttendIi;
    @InjectView(R.id.iv_img_ii)
    ImageView ivImgIi;
    @InjectView(R.id.activity_punch_details)
    LinearLayout activityPunchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_details);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_img_i, R.id.iv_img_ii})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_img_i:
                break;
            case R.id.iv_img_ii:
                break;
        }
    }
}
