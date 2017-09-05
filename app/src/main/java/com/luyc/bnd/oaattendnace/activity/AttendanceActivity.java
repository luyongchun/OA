package com.luyc.bnd.oaattendnace.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.view.MyCircleView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AttendanceActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 100;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.tv_company)
    TextView tvCompany;
    @InjectView(R.id.tv_help)
    TextView tvHelp;
    @InjectView(R.id.iv_header)
    ImageView ivHeader;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.tv_attendance)
    TextView tvAttendance;
    @InjectView(R.id.iv_go_work)
    ImageView ivGoWork;
    @InjectView(R.id.activity_attendance)
    LinearLayout activityAttendance;
    @InjectView(R.id.mcv_card)
    MyCircleView mcvCard;
    @InjectView(R.id.tv_card)
    TextView tvCard;
    @InjectView(R.id.tv_now_time)
    TextView tvNowTime;
    @InjectView(R.id.rl_card)
    RelativeLayout rlCard;
    @InjectView(R.id.tv_address)
    TextView tvAddress;
    @InjectView(R.id.tv_location)
    TextView tvLocation;
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
                    tvNowTime.setText(sysTimeStr);
                    break;
                default:
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.inject(this);
        getSystemTime();
        new TimeThread().start();
    }
    class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }


    private void getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA);
        String now = sdf.format(new Date());
        tvTime.setText(now.substring(0,11));
        tvNowTime.setText(now.substring(11));
    }

    @OnClick({R.id.iv_back, R.id.tv_help, R.id.tv_name,
            R.id.tv_time, R.id.tv_attendance,R.id.rl_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_help:
                break;
            case R.id.tv_name:
                break;
            case R.id.tv_attendance:
                break;
            case R.id.rl_card:
                setPermision();
                break;

            case R.id.mcv_card:

                break;
        }
    }
    private void setPermision(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
             ActivityCompat.requestPermissions(this,
             new String[]{Manifest.permission.CAMERA},
             REQUEST_TAKE_PHOTO_PERMISSION);  }
             else  {
            Intent intent = new Intent(this, CustomerCameraActivity.class);
            startActivity(intent);
                       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                Intent intent = new Intent(this, CustomerCameraActivity.class);
                startActivity(intent);
            } else {
             Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show();      }
                     return;       }
                 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
