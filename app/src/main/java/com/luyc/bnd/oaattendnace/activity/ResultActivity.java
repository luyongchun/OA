package com.luyc.bnd.oaattendnace.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.utils.ImageUtil;
import com.luyc.bnd.oaattendnace.utils.MyToastShow;
import com.luyc.bnd.oaattendnace.utils.MyToos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public static final String ACTION = "FINISH.ATTENDANCE_CARD";
    private int cameraPosition = 1;
    private String nowData;
    private String nowTime;
    private String userName = "小木匠";
    private String userLocation = "";
    private boolean isCamera;
    private String TAG = "ResultActivity";
    private String address, filePath;
    private Bitmap btm;
    private String compay = "数控汇OA考勤";
    private byte[] datas;
    final static String SERVICE_NS = "http://ws.platform.telezone.com/";
    final static String SERVICE_URL = "http://192.168.1.57:9090/platform-webapp/services/pdaAssetWebService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        filePath = bundle.getString("filePath");
        nowData = bundle.getString("nowData");
        nowTime = bundle.getString("nowTime");
        address = bundle.getString("address");
        isCamera = bundle.getBoolean("isCamera");
        datas = bundle.getByteArray("data");
        userLocation = address;

        Log.e(TAG, "onCreate:address== " + address);
        showPic2ImageView(filePath);

    }

    private void showPic2ImageView(String filePath) {


        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if (!TextUtils.isEmpty(filePath)) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Matrix matrix = new Matrix();
                //通过Matrix把图片旋转90度
                if (isCamera) {
                    matrix.setRotate(270);
                } else {
                    matrix.setRotate(90);
                }
                //给照片打上水印
                btm = drawWaterBitmap(width, bitmap, matrix);
                sfvCamera.setImageBitmap(btm);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "图片路径有误", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private Bitmap drawWaterBitmap(int width, Bitmap bitmap, Matrix matrix) {
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        Bitmap btm1 = ImageUtil.drawTextToLeftTop(this, bitmap, nowTime, 35, Color.WHITE, 15, 15);
        Bitmap btm2 = ImageUtil.drawTextToLeftTop(this, btm1, nowData, 20, Color.WHITE, 10, 50);
        Bitmap btm3 = ImageUtil.drawTextToLeftTop(this, btm2, compay, 20, Color.WHITE, 10, 70);
        int drawTextWidth = (int) ImageUtil.drawTextWidth(userLocation);

        Log.e(TAG, "showPic2ImageView: drawTextWidth//width//bitmap.getWidth()"
                + drawTextWidth + "//" + width + "//" + bitmap.getWidth());
        if (drawTextWidth > 350) {
            userLocation = userLocation.substring(0, 28);
            userLocation = userLocation + "...";
        }
        Bitmap btm4 = ImageUtil.drawTextToRightBottom(this, btm3, userLocation, 22, Color.WHITE, 10, 20);
        Bitmap btm5 = ImageUtil.drawTextToRightBottom(this, btm4, userName, 22, Color.WHITE, 20, 50);
        Bitmap btm = ImageUtil.createWaterMaskRightBottom(this, btm5,
                BitmapFactory.decodeResource(getResources(), R.mipmap.location_ii), ((int) ImageUtil.drawTextWidth(userLocation)) * 2 - 40, 18);

        return ImageUtil.createWaterMaskRightBottom(this, btm,
                BitmapFactory.decodeResource(getResources(), R.mipmap.me_ii), ((int) ImageUtil.drawTextWidth(userName)) * 2 + 15, 48);
    }


    @OnClick({R.id.tv_cancel, R.id.tv_user_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                Intent intent = new Intent(this, CustomerCameraActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                bundle.putString("mapTime", nowData + nowTime);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_user_photo:
                MyToos myToos = new MyToos(this);
                boolean netWorkStatle = myToos.isNetWorkStatle();
                //将图片上传到服务器
                if (netWorkStatle) {
                    new Thread() {
                        @Override
                        public void run() {
                            String methodName = "saveKqGps";
                            //创建httpTransportSE传输对象
                            HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
                            // ht.debug = true;
                            //使用soap1.1协议创建Envelop对象
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            //实例化SoapObject对象
                            SoapObject request = new SoapObject(SERVICE_NS, methodName);
                            /**
                             * 设置参数，参数名不一定需要跟调用的服务器端的参数名相同，只需要对应的顺序相同即可
                             * */
                            //saveKqGps(@WebParam(name="gpsCode")String gpsCode,@WebParam(name="lat")String lat,
                            // @WebParam(name="lng")String lng,@WebParam String kq_date)
                            request.addProperty("gpsCode", "110120");
                            request.addProperty("lat", "11");
                            request.addProperty("lng", "12");
                            request.addProperty("kq_date", "20171014");
                            // request.addProperty("password",password.getText().toString());
                            //将SoapObject对象设置为SoapSerializationEnvelope对象的传出SOAP消息
                            envelope.bodyOut = request;
                            try {
                                //调用webService
                                ht.call(null, envelope);
                                //
                                //                            if(envelope.getResponse() != null){
                                //                                SoapObject result = (SoapObject) envelope.bodyIn;
                                //                                String name = envelope.getResult().toString();
                                //                                //String name= (String) result.getProperty("return");
                                //                                //txt1.setText("返回值 = "+name);
                                //                                //resultView.setText(name);
                                //                                Log.e("mainActivity", "name=="+name);
                                //                                if(name.equals("1")){
                                //                                    Intent intent=new Intent(MainActivity.this,MainSecondActivity.class);
                                //                                    intent.putExtra("userNum", username.getText().toString());
                                //                                    startActivity(intent);
                                //                                    finish();
                                //                                }else{
                                //                                    //DialogUtil.showDialog(this, "用户名或者密码错误，请重新输入!", false);
                                //
                                //                                };
                                //                            }else{
                                //
                                //                            }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    Intent receiver = new Intent(ACTION);
                    sendBroadcast(receiver);
                    File file = new File(filePath);
                    try {
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        btm.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                        fos.flush();
                        fos.close();
                        finish();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else {
                    MyToastShow.showToast(this,"当前网络不可用，请先检查您的网络连接哦");
                }
                break;
        }
    }

}
