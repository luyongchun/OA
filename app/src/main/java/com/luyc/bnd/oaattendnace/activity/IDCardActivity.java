package com.luyc.bnd.oaattendnace.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.utils.FileUtil;
import com.luyc.bnd.oaattendnace.utils.MyToastShow;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IDCardActivity extends AppCompatActivity {

    @InjectView(R.id.ll_card_front)
    LinearLayout llCardFront;
    @InjectView(R.id.ll_card_back)
    LinearLayout llCardNative;
    @InjectView(R.id.tv_result)
    TextView tvResult;
    @InjectView(R.id.ll_result)
    LinearLayout llResult;
    private boolean hasGotToken;
    private AlertDialog.Builder alertDialog;
    private int REQUEST_CODE_CAMERA = 222;
    private boolean isID_CARD_FRONT = false, isID_CARD_BACK = false;
    private String AK = "GrgQ4HkTXcvomE993Tsc5ZBd";
    private String SK = "I89b4glboBIGafYPZWgv6WN1OWzKZrGo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        ButterKnife.inject(this);
        alertDialog = new AlertDialog.Builder(this);
        initAccessTokenWithAkSk();//初始化OCR
    }

    @OnClick({R.id.ll_card_front, R.id.ll_card_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_card_front:

                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(this, CameraActivity.class);
                // 设置临时存储
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(this).getAbsolutePath());
                // 调用拍摄身份证正面（不带本地质量控制）activity
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
                break;
            case R.id.ll_card_back:
                Intent i = new Intent(IDCardActivity.this, CameraActivity.class);
                i.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                i.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(i, REQUEST_CODE_CAMERA);
                break;
        }
    }

    private void initAccessToken() {

        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, this);
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, this, AK, SK);
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                llResult.setVisibility(View.VISIBLE);
                                tvResult.setText(message);
                            }
                        })

                        .show();
            }
        });
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            MyToastShow.showToast(this, "token还未成功获取");
        }
        return hasGotToken;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取调用参数
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
            // 获取图片文件调用sdk数据接口，见数据接口说明
            if (data != null) {
                String filePath = FileUtil.getSaveFile(this).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        isID_CARD_FRONT = true;
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        isID_CARD_BACK = true;
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
    }

    private void recIDCard(String idCardSide, final String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    try {
                        if (isID_CARD_FRONT) {
                            String birthday = result.getBirthday().toString();
                            alertText("扫描结果：\n",
                                    "姓名：" + result.getName().toString() + "\n"
                                            + "性别：" + result.getGender().toString() + "\n"
                                            + "民族：" + result.getEthnic().toString() + "\n"
                                            + "出生：" + birthday.substring(0, 4) + "年"
                                            + birthday.substring(4, 6) + "月"
                                            + birthday.substring(6) + "日" + "\n"
                                            + "地址：" + result.getAddress().toString() + "\n"
                                            + "身份证号码：" + result.getIdNumber() + "\n");
                            isID_CARD_FRONT = false;
                        } else if (isID_CARD_BACK) {
                            String expiryDate = result.getExpiryDate().toString();
                            String signDate = result.getSignDate().toString();
                            alertText("扫描结果：\n",
                                    "签发机关：" + result.getIssueAuthority().toString() + "\n"
                                            + "有效期：" + signDate.substring(0, 4) + "年"
                                            + signDate.substring(4, 6) + "月"
                                            + signDate.substring(6) + "日" + "\t到" + "\n"
                                            +"\t\t\t\t\t\t\t\t" +expiryDate.substring(0, 4) + "年"
                                            + expiryDate.substring(4, 6) + "月" +
                                            expiryDate.substring(6) + "日" + "\n"
                            );
                            isID_CARD_BACK = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        alertText("扫描结果异常,结果如下:\n", result.toString());
                    }
//                    alertText("", result.toString());
                }
            }

            @Override
            public void onError(OCRError error) {
                alertText("", "识别错误，错误信息如下：\n" + error.getMessage());
            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }
}
