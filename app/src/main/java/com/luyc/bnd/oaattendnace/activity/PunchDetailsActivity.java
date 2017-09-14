package com.luyc.bnd.oaattendnace.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PunchDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.iv_back) ImageView ivBack;
    @InjectView(R.id.iv_attend) ImageView ivAttend;
    @InjectView(R.id.tv_attendance_time_i) TextView tvAttendanceTimeI;
    @InjectView(R.id.tv_company_work_time) TextView tvCompanyWorkTime;
    @InjectView(R.id.tv_addressed) TextView tvAddressed;
    @InjectView(R.id.tv_work_nor_i) TextView tvWorkNorI;
    @InjectView(R.id.tv_updata_attend_i) TextView tvUpdataAttendI;
    @InjectView(R.id.iv_img_i) ImageView ivImgI;
    @InjectView(R.id.iv_attend_i) ImageView ivAttendI;
    @InjectView(R.id.tv_attendance_time_ii) TextView tvAttendanceTimeIi;
    @InjectView(R.id.textView5) TextView textView5;
    @InjectView(R.id.tv_addressed_i) TextView tvAddressedI;
    @InjectView(R.id.tv_work_nor_ii) TextView tvWorkNorIi;
    @InjectView(R.id.tv_updata_attend_ii) TextView tvUpdataAttendIi;
    @InjectView(R.id.iv_img_ii) ImageView ivImgIi;
    @InjectView(R.id.activity_punch_details) LinearLayout activityPunchDetails;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_details);
        showBigBitmap();
        ButterKnife.inject(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_img_i, R.id.iv_img_ii})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_img_i:
                dialog.show();
                break;
            case R.id.iv_img_ii:
                dialog.show();
                break;
        }
    }

    //动态设置iamgview
    private ImageView getImagView() {
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        InputStream is = getResources().openRawResource(R.mipmap.ic_launcher);
        Drawable drawable = BitmapDrawable.createFromStream(is, null);
        iv.setImageDrawable(drawable);
//        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        return iv;

    }
    private void showBigBitmap() {
        dialog = new Dialog(this, R.style.AlertDialog_AppCompat_Light_);
        final ImageView imagView = getImagView();
        dialog.setContentView(imagView);
        imagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        imagView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //弹出的“保存图片”的Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PunchDetailsActivity.this);
                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveCroppedImage(((BitmapDrawable) imagView.getDrawable()).getBitmap());
                    }
                });
                builder.show();
                return true;
            }
        });
    }
    //保存图片
    private void saveCroppedImage(Bitmap bmp) {
        File file = new File("/sdcard/OA");
        if (!file.exists())
            file.mkdir();

        file = new File("/sdcard/"+System.currentTimeMillis()+"temp.jpg".trim());
        String fileName = file.getName();
        String mName = fileName.substring(0, fileName.lastIndexOf("."));
        String sName = fileName.substring(fileName.lastIndexOf("."));

        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = "/sdcard/OA" + "/" + mName + "_cropped" + sName;
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this,"图片保存成功",Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
