package com.luyc.bnd.oaattendnace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.adapter.AllRecordRCVAdapter;
import com.luyc.bnd.oaattendnace.adapter.RecycleViewAdapter;
import com.luyc.bnd.oaattendnace.utils.StatusBarCompat;
import com.luyc.bnd.oaattendnace.view.ProcessView;
import com.luyc.bnd.oaattendnace.widget.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

public class ReleaseActivity extends AppCompatActivity {

    @InjectView(R.id.ll_releas_back) LinearLayout llReleasBack;
    @InjectView(R.id.id_process) ProcessView idProcess;
    @InjectView(R.id.et_title) EditText etTitle;
    @InjectView(R.id.rl_flow) RelativeLayout rlFlow;
    @InjectView(R.id.et_cause) EditText etCause;
    @InjectView(R.id.et_name) EditText etName;
    @InjectView(R.id.rl_type) RelativeLayout rlType;
    @InjectView(R.id.tv_start_time) TextView tvStartTime;
    @InjectView(R.id.rl_start_time) RelativeLayout rlStartTime;
    @InjectView(R.id.tv_end_time) TextView tvEndTime;
    @InjectView(R.id.rl_end_time) RelativeLayout rlEndTime;
    @InjectView(R.id.et_releas_time) EditText etReleasTime;
    @InjectView(R.id.rb_discommit) RadioButton rbDiscommit;
    @InjectView(R.id.rb_commit) RadioButton rbCommit;
    @InjectView(R.id.iv_i) ImageView ivI;
    @InjectView(R.id.iv_ii) ImageView ivIi;
    @InjectView(R.id.sp_type) Spinner spType;
    @InjectView(R.id.tv_flow) TextView tvFlow;
    @InjectView(R.id.tv_bartop) TextView tvBartop;
    @InjectView(R.id.tv_because) TextView tvBecause;
    @InjectView(R.id.tv_name) TextView tvName;
    @InjectView(R.id.tv_type) TextView tvType;

    private CustomDatePicker customDatePicker1, customDatePicker2;
    private String TAG = "ReleaseActivity";
    private ArrayList<String> list = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private RadioButton rbStartTime;
    private RadioButton rbEndTime;
    private ImageView ivBack;
    private ProcessView process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle matterType = intent.getBundleExtra("matterType");
        int type = matterType.getInt("type");
        list.add("请假");
        list.add("加班");
        list.add("外出");
        list.add("出差");
        Log.e(TAG, "onCreate: list.size()-1///type" + (list.size() - 1) + "///" + type);
        StatusBarCompat.compat(this, R.color.white);
        if (type == list.size()) {
            setContentView(R.layout.activity_apply);
            recyclerView = ((RecyclerView) findViewById(R.id.recycler));
            rbStartTime = ((RadioButton) findViewById(R.id.startTimeByRb));
            rbEndTime = ((RadioButton) findViewById(R.id.endTimeByRb));
            ivBack = ((ImageView) findViewById(R.id.iv_back));
            initDataByRecyclerView();
            initDatePicker(1);
            rbStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDatePicker1.show(rbStartTime.getText().toString());

                }
            });

            rbEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDatePicker2.show(rbEndTime.getText().toString());
                }
            });
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        } else {
            setContentView(R.layout.activity_release);
            ButterKnife.inject(this);
            process = ((ProcessView) findViewById(R.id.id_process));
            process.setApprovalProcessText(ApprovalProcessActivity.userNmes,
                    ApprovalProcessActivity.userDepartment,ApprovalProcessActivity.userState);
            tvBartop.setText(list.get(type));
            tvFlow.setText(list.get(type));
            initDownMenu(type);
            initDatePicker(-1);
        }

    }

    private void initDataByRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AllRecordRCVAdapter adapter = new AllRecordRCVAdapter(this, list);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerViewItemClick(new RecycleViewAdapter.onRecycleViewItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(ReleaseActivity.this, "点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(ReleaseActivity.this, "长按", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDatePicker(final int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        String nowTime = format.format(new Date());
        String text = nowTime + "-01 00:00";
        if (i == -1) {
            tvStartTime.setText(text);
            tvEndTime.setText(now);
        } else {
            rbStartTime.setText(text);
            rbEndTime.setText(now);
        }

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
//                startTimeByRb.setText(time.split(" ")[0]);
                if (i == -1) {
                    tvStartTime.setText(time);
                } else {
                    rbStartTime.setText(time);
                }

            }
        }, "2010-01-01 00:00", now, -1); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 是否显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
//                endTimeByRb.setText(time.split(" ")[0]);
                if (i == -1) {
                    tvEndTime.setText(time);
                } else {
                    rbEndTime.setText(time);
                }
            }
        }, "2010-01-01 00:00", now, -1); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 是否显示时和分
        customDatePicker2.setIsLoop(false); // 不允许循环滚动

    }

    //初始化菜单
    private void initDownMenu(int type) {
        ArrayAdapter<String> typeAdapter = null;
        switch (type) {
            case 0:
                typeAdapter = new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.ReleaseFlow));
                break;
            case 1:
                tvBecause.setText("加班理由:");
                tvName.setText("加班人员:");
                tvType.setText("加班类型:");
                typeAdapter = new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.ReleaseType));
                break;
            case 2:
                tvBecause.setText("外出理由:");
                tvName.setText("外出人员:");
                tvType.setText("外出类型:");
                typeAdapter = new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.ReleaseType_i));
                break;
            case 3:
                tvBecause.setText("出差理由:");
                tvName.setText("出差人员:");
                tvType.setText("出差类型:");
                typeAdapter = new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.ReleaseType_ii));
                break;

        }
        typeAdapter.setDropDownViewResource(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);
    }

    @Optional
    @OnClick({R.id.ll_releas_back, R.id.et_title, R.id.et_cause, R.id.et_name,
            R.id.rl_start_time, R.id.rl_end_time, R.id.et_releas_time,
            R.id.rb_discommit, R.id.rb_commit, R.id.startTimeByRb,
            R.id.endTimeByRb, R.id.recycler})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_releas_back:
                finish();
                break;
            case R.id.et_title:
                break;
            case R.id.et_cause:
                break;
            case R.id.et_name:
                break;
            case R.id.rl_start_time:
                customDatePicker1.show(tvStartTime.getText().toString());
                break;
            case R.id.rl_end_time:
                customDatePicker2.show(tvEndTime.getText().toString());
                break;
            case R.id.et_releas_time:
                break;
            case R.id.rb_discommit:
                finish();
                break;
            case R.id.rb_commit:
                Toast.makeText(this, "正在提交您的请假申请，请稍等", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;


        }
    }

}
