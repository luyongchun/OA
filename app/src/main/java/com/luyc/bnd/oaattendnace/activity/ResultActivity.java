package com.luyc.bnd.oaattendnace.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.utils.ImageUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity {


    @InjectView(R.id.activity_result)
    RelativeLayout activityResult;
    @InjectView(R.id.sfv_camera)
    ImageView sfvCamera;
    @InjectView(R.id.tv_cancel)
    TextView tvCancel;
    @InjectView(R.id.tv_user_photo)
    TextView tvUserPhoto;

    private int cameraPosition = 1;
    private String nowData;
    private String nowTime;
    private String userName="陆永春";
    private String userLocation="广东省佛山市顺德区大良街道S43广珠西线高速";
    private boolean isCamera;
    private String TAG="ResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("filePath");
        nowData = intent.getStringExtra("nowData");
        nowTime = intent.getStringExtra("nowTime");
        isCamera = intent.getBooleanExtra("isCamera",false);
        Log.e(TAG, "onCreate:isCamera== "+isCamera );
        showPic2ImageView(filePath);

    }

    private void showPic2ImageView(String filePath) {

        if (!TextUtils.isEmpty(filePath)) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Matrix matrix = new Matrix();
                //通过Matrix把图片旋转90度
                if (isCamera){
                    matrix.setRotate(-90);
                }else {
                    matrix.setRotate(90);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Bitmap btm1 = ImageUtil.drawTextToLeftTop(this, bitmap, nowTime, 35, Color.WHITE, 10, 15);
                Bitmap btm2 = ImageUtil.drawTextToLeftTop(this, btm1, nowData, 20, Color.WHITE, 10, 50);

                Bitmap btm4 = ImageUtil.drawTextToRightBottom(this, btm2, userLocation, 22, Color.WHITE, 10, 20);

                Bitmap btm5 = ImageUtil.drawTextToRightBottom(this, btm4, userName, 22, Color.WHITE, 10, 50);

                Bitmap btm = ImageUtil.createWaterMaskRightBottom(this, btm5,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.location), ((int) ImageUtil.drawTextWidth(userLocation))*2-20, 18);

                Bitmap btm3 = ImageUtil.createWaterMaskRightBottom(this, btm,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.me), ((int) ImageUtil.drawTextWidth(userName))*2+5, 48);

                sfvCamera.setImageBitmap(btm3);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this,"图片路径有误",Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({R.id.tv_cancel, R.id.tv_user_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                startActivity(new Intent(this,CustomerCameraActivity.class));
                finish();
                break;
            case R.id.tv_user_photo:
                finish();
                break;
        }
    }
}
