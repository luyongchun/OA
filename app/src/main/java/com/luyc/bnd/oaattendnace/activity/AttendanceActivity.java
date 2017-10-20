package com.luyc.bnd.oaattendnace.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.utils.MyToastShow;
import com.luyc.bnd.oaattendnace.utils.MyToos;
import com.luyc.bnd.oaattendnace.utils.Service;
import com.luyc.bnd.oaattendnace.view.MyCircleView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AttendanceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 11;
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

    @InjectView(R.id.tv_work_attendace)
    TextView tvWorkAttendace;
    @InjectView(R.id.tv_again_location)
    TextView tvAgainLocation;
    @InjectView(R.id.tv_attendance_address)
    TextView tvAttendanceAddress;
    @InjectView(R.id.iv_attendance)
    ImageView ivAttendance;
    @InjectView(R.id.rl_all_card)
    RelativeLayout rlAllCard;
    @InjectView(R.id.iv_attend)
    ImageView ivAttend;
    @InjectView(R.id.tv_attendance_time)
    TextView tvAttendanceTime;
    @InjectView(R.id.tv_company_work_time)
    TextView tvCompanyWorkTime;
    @InjectView(R.id.tv_addressed)
    TextView tvAddressed;
    @InjectView(R.id.tv_work_nor)
    TextView tvWorkNor;
    @InjectView(R.id.tv_look)
    TextView tvLook;
    @InjectView(R.id.tv_updata_attend)
    TextView tvUpdataAttend;
    @InjectView(R.id.iv_attend_i)
    ImageView ivAttendI;
    @InjectView(R.id.tv_attendance_time_i)
    TextView tvAttendanceTimeI;
    @InjectView(R.id.tv_addressed_i)
    TextView tvAddressedI;
    @InjectView(R.id.tv_work_nor_i)
    TextView tvWorkNorI;
    @InjectView(R.id.tv_look_i)
    TextView tvLookI;
    @InjectView(R.id.tv_updata_attend_i)
    TextView tvUpdataAttendI;
    @InjectView(R.id.ll_gowork_i)
    LinearLayout llGoworkI;
    @InjectView(R.id.ll_after_ii)
    LinearLayout llAfterIi;
    @InjectView(R.id.tv_horizontal)
    TextView tvHorizontal;
    //    @InjectView(R.id.tv_orizontal)
//    TextView tvOrizontal;
    private PopupWindow popupWindow, mPopupWindow, aPopupWindow, iPopupWindow;
    private String address = "", date;
    private String mapTime = "";//地图时间
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private String TAG = "AttendanceActivity";
    private double mLatitude = 22.811013, mLongittude = 113.333643, mAccuracy = 550;//固定经度纬度以及精确度
    private double latitude, longitude;
    private boolean netWorkStatle, isFirst;
    private String succeedTime, backAddress = "", mapAddress = "";
    private MyToos myToos;
    private Dialog dialog;
    private MyCircleView mcv;
    private long parseInt = -1, ServiceT = -1;
    private String toStamp = "", serviceDate = "", serviceTime = "";
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //可在其中解析amapLocation获取相应内容。
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    initAddressAndView(aMapLocation);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    private void initAddressAndView(AMapLocation aMapLocation) {
        address = aMapLocation.getAddress();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA);
        mapTime = sdf.format(new Date(aMapLocation.getTime()));//获取地图时间

        Log.e(TAG, "initAddressAndView: now==" + mapTime);
        String street = aMapLocation.getStreet();//街道
        String streetNum = aMapLocation.getStreetNum();//街道号

        latitude = aMapLocation.getLatitude();//获取纬度
        longitude = aMapLocation.getLongitude();//获取经度
        float accuracy = aMapLocation.getAccuracy();//获取定位精度信息
        LatLng start = new LatLng(22.811404, 113.33247);//固定对比的经度和纬度
        LatLng end = new LatLng(latitude, longitude);
        float v = AMapUtils.calculateLineDistance(start, end);//比较两点之间的距离
        float v1 = AMapUtils.calculateArea(start, end);//面积

        int distance = (int) getDistance(start, end);

        Log.e(TAG, "onLocationChanged: v//distance//v1==" + v + "//" + distance + "//" + v1);
        String mAddress = street + streetNum;
//        distance=500;

        if (!mAddress.equals("")) {
            if (distance <= 400) {
                mcv.setPaintColor(1);
//                myCircleView.setPaintColor(1);
                tvAttendanceAddress.setText("您已进入考勤范围:");
                tvAttendanceAddress.setTextColor(getResources().getColor(R.color.text2));
                if (mapAddress.equals("")) {
                    tvAddress.setText(mAddress);
                } else {
                    tvAddress.setText(mapAddress);
                }
                ivAttendance.setImageResource(R.mipmap.ok_ii);
            } else {
                mcv.setPaintColor(-1);
//                tvCard.setText("外勤打卡");
                tvAttendanceAddress.setText("当前不在考勤范围内:");
                tvAttendanceAddress.setTextColor(getResources().getColor(R.color.color_later));
//                tvAddress.setText(mAddress);
                if (mapAddress.equals("")) {
                    tvAddress.setText(mAddress);
                } else {
                    tvAddress.setText(mapAddress);
                }
                ivAttendance.setImageResource(R.mipmap.warring_i);
            }
        } else {
            tvAttendanceAddress.setText("当前信号较弱 ");
            tvAttendanceAddress.setTextColor(getResources().getColor(R.color.color_ii));
            tvAddress.setText("");
            ivAttendance.setImageResource(R.mipmap.warring);
        }

        Log.e(TAG, "address: street//streetNum//纬度//经度//精度"
                + street + "\n" + streetNum + "\n" + latitude + "\n" + longitude + "\n" + accuracy);
    }

    private double getDistance(LatLng start, LatLng end) {

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;
        return d * 1000;
    }

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (netWorkStatle && !toStamp.equals("")) {
                        if (!toStamp.equals("") && isFirst) {
                            parseInt = Long.parseLong(toStamp);
                            ServiceT = parseInt + 1000;
                            isFirst = false;
                        } else {
                            ServiceT += 1000;
                        }

                        date = stampToDate(ServiceT + "");
                        Log.e("test", "handleMessage:date/toStamp== " + date + "/" + ServiceT);
                        serviceDate = date.substring(0, 11);
                        serviceTime = date.substring(11);
                        tvNowTime.setText(serviceTime);
                        tvTime.setText(serviceDate.replaceAll("-", "."));
                        Log.e("test", "handleMessage: date==" + date);
                    } else {
                        long sysTime = System.currentTimeMillis();
                        CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
                        tvNowTime.setText(sysTimeStr);
                    }
                    break;
                default:
                    break;
            }

        }
    };
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> mlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.inject(this);
        mcv = ((MyCircleView) findViewById(R.id.mcv_card));
        getSystemTime();

        //检查网络
        myToos = new MyToos(this);
        netWorkStatle = myToos.isNetWorkStatle();
        //初始化地图
        mLocationClient = new AMapLocationClient(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //注册广播接收器
        initReceiver();
        initData();//初始化数据
        if (netWorkStatle) {
            getServiceSysTime();
            requestLocationAdrees(-1);//请求定位
        } else {
            MyToastShow.showToast(this, "当前网络不可用，请先检查您的网络连接哦");
        }
        new TimeThread().start();//开启定时器
    }

    private void getServiceSysTime() {
        new Thread() {
            @Override
            public void run() {
                String methodName = "sysn_time";
                //创建httpTransportSE传输对象
                HttpTransportSE ht = new HttpTransportSE(Service.SERVICE_URL);
                // ht.debug = true;
                //使用soap1.1协议创建Envelop对象
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                //实例化SoapObject对象
                SoapObject request = new SoapObject(Service.SERVICE_NS, methodName);
                /**
                 * 设置参数，参数名不一定需要跟调用的服务器端的参数名相同，只需要对应的顺序相同即可
                 * */
                //将SoapObject对象设置为SoapSerializationEnvelope对象的传出SOAP消息
                envelope.bodyOut = request;

                try {
                    //调用webService
                    ht.call(null, envelope);
                    if (envelope.getResponse() != null) {
                        SoapObject result = (SoapObject) envelope.bodyIn;
                        String sys = envelope.getResult().toString().trim();
                        Log.e(TAG, "run: sysssss" + sys);
//                        String time = sys.substring(11);
                        toStamp = dateToStamp(sys);
                        isFirst = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /*
   * 将时间转换为时间戳
   */
    public static String dateToStamp(String s) {
        String res = null;
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            Log.e("test", "dateToStamp: ts" + ts);
            res = String.valueOf(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /*
    * 将时间戳转换为时间
 ```*/
    public static String stampToDate(String s) {
        String res = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            long lt = new Long(s);
            date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ResultActivity.ACTION_SUCCEED);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void initData() {
        list.add("1111");
//        list.add("1111");
        mlist.add("1");
        if (list.size() == 0) {
            llGoworkI.setVisibility(View.GONE);
            llAfterIi.setVisibility(View.GONE);
        } else if (list.size() == 1) {
            llGoworkI.setVisibility(View.VISIBLE);
            llAfterIi.setVisibility(View.GONE);
        } else if (list.size() > 1) {
            tvHorizontal.setVisibility(View.VISIBLE);
            rlAllCard.setVisibility(View.GONE);
            llGoworkI.setVisibility(View.VISIBLE);
            llAfterIi.setVisibility(View.VISIBLE);
        }
    }

    private void requestLocationAdrees(int requestType) {
        if (requestType != -1) {
            mLocationClient = new AMapLocationClient(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
        }
        if (MyToos.isLOLLIPOP()) {
            if (ContextCompat.checkSelfPermission(AttendanceActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AttendanceActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                initLocationValues();
            }
        } else {
            initLocationValues();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    private void initLocationValues() {

        mLocationClient.setLocationListener(mLocationListener);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
//        Toast.makeText(this, "正在定位，请稍等...", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_btm:
                dialog.show();
                break;
            case R.id.tv_colse:
                popupWindow.dismiss();
                break;
            case R.id.tv_know:
                mPopupWindow.dismiss();
                break;
            case R.id.tv_cancel:
                aPopupWindow.dismiss();
                break;
            case R.id.tv_updata:
                boolean netWorkStatle = myToos.isNetWorkStatle();
                if (netWorkStatle){
                    getServiceSysTime();
                    setPermision();
                }else {
                    MyToastShow.showToast(this,"哎呀，又塞网了，请先检查您的网络哦");
                }
                aPopupWindow.dismiss();
                break;
            case R.id.iv_img:
                iPopupWindow.dismiss();
                break;

        }
    }

    private void showBigBitmap() {
        dialog = new Dialog(AttendanceActivity.this, R.style.AlertDialog_AppCompat_Light_);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
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

    //动态设置iamgview
    private ImageView getImagView() {
        ImageView iv = new ImageView(AttendanceActivity.this);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        InputStream is = getResources().openRawResource(R.mipmap.ic_launcher);
        Drawable drawable = BitmapDrawable.createFromStream(is, null);
        iv.setImageDrawable(drawable);
//        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        return iv;

    }

    //保存图片
    private void saveCroppedImage(Bitmap bmp) {
        File file = new File("/sdcard/myOA");
        if (!file.exists())
            file.mkdir();

        file = new File("/sdcard/" + System.currentTimeMillis() + "temp.jpg".trim());
        String fileName = file.getName();
        String mName = fileName.substring(0, fileName.lastIndexOf("."));
        String sName = fileName.substring(fileName.lastIndexOf("."));

        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = "/sdcard/myOA" + "/" + mName + "_cropped" + sName;
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    class TimeThread extends Thread {
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
        Log.e(TAG, "getSystemTime:now========== " + now);
        tvTime.setText(now.substring(0, 11));
        String time = now.substring(11);
        tvNowTime.setText(time);
        succeedTime = time.substring(0, 5);
        Log.e(TAG, "getSystemTime: str" + succeedTime);
//        int parseTime = Integer.parseInt(str);
        int parseTime = 13;
        if (parseTime < 12) {
            tvCard.setText("上班打卡");
            tvWorkAttendace.setText("上班时间 08：30");
            ivGoWork.setImageResource(R.mipmap.go_work_i);
        } else {
            tvCard.setText("下班打卡");
            tvWorkAttendace.setText("下班时间 17：30");
            ivGoWork.setImageResource(R.mipmap.after_work_ii);
        }

    }

    @OnClick({R.id.iv_back, R.id.tv_help, R.id.tv_again_location, R.id.rl_card,
            R.id.tv_updata_attend, R.id.tv_updata_attend_i, R.id.tv_look, R.id.tv_look_i})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_help:
                Toast.makeText(this, "正在努力建设哦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_again_location:
                Intent intent = new Intent(this, MapActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.rl_card:
                boolean netWorkStatle = MyToos.isNetWorkStatle();
                if (netWorkStatle){
                    getServiceSysTime();
                    setPermision();
                }else {
                    MyToastShow.showToast(this,"哎呀，塞网了，请先检查您的网络哦");
                }

                break;
            case R.id.tv_updata_attend:
                showUpdataPopupWindow();
                break;
            case R.id.tv_updata_attend_i:
                showUpdataPopupWindow();
                break;
            case R.id.tv_look:
                showPopupWindow();
                break;
            case R.id.tv_look_i:
                showPopupWindow();
                break;
        }
    }

    //结果回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            backAddress = data.getStringExtra("address");
            mapAddress = data.getStringExtra("mAddress");
            requestLocationAdrees(1);
        }
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (!mapAddress.equals("")) {
                    tvAddress.setText(mapAddress);
                } else if (!address.equals("")) {
                    tvAddress.setText(address);
                } else {
                    tvAddress.setText("");
                }
                break;
        }
    }

    //相机权限申请
    private void setPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_TAKE_PHOTO_PERMISSION);
        } else {
            if (address.equals("")) {
                Toast.makeText(this, "没有获取到当前位置信息，请您重新定位", Toast.LENGTH_SHORT).show();
                return;
            }
            openCCActivity();
        }
    }

    private void openCCActivity() {
        if (address == null) {
            Toast.makeText(this, "当前获取到的地址为空，请您重新定位", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, CustomerCameraActivity.class);
        Bundle bundle = new Bundle();
        String replace = date.replace("-", ".");
        bundle.putString("mapTime", replace);
        Log.e(TAG + "ssss", "openCCActivity: date.replace==" + replace);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        if (backAddress.equals("")) {
            bundle.putString("address", address);
        } else {
            bundle.putString("address", backAddress);
        }

        Log.e(TAG, "setPermision: address" + address);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    private void showPopupWindow() {

        View view = LayoutInflater.from(this).inflate(R.layout.popupwindows_attendance, null);
        TextView tvColse = (TextView) view.findViewById(R.id.tv_colse);
        ImageView ivBtm = (ImageView) view.findViewById(R.id.iv_btm);
        TextView address = (TextView) view.findViewById(R.id.tv_pop_attendance_address);
        TextView time = (TextView) view.findViewById(R.id.tv_pop_attendance_time);
        address.setText(this.address);
        time.setText(succeedTime);
        ivBtm.setOnClickListener(this);
        tvColse.setOnClickListener(this);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tvColse.setOnClickListener(this);
        setPopupWindow(popupWindow, view);
        showBigBitmap();
    }

    private void showImgPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindows_img, null);
        ImageView img = (ImageView) view.findViewById(R.id.iv_img);
        img.setOnClickListener(this);
        iPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        setPopupWindow(iPopupWindow, view);

    }

    private void showUpdataPopupWindow() {

        View view = LayoutInflater.from(this).inflate(R.layout.popup_updata_attendance, null);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvUpdata = (TextView) view.findViewById(R.id.tv_updata);

        tvCancel.setOnClickListener(this);
        tvUpdata.setOnClickListener(this);
        aPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tvCancel.setOnClickListener(this);
        tvUpdata.setOnClickListener(this);
        setPopupWindow(aPopupWindow, view);

    }

    private void showSucceedPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.succeed_attendance, null);

        TextView tvSucceed = (TextView) view.findViewById(R.id.tv_succeed_time);
        TextView tvKnow = (TextView) view.findViewById(R.id.tv_know);
        TextView tvWorkType = (TextView) view.findViewById(R.id.tv_work_type);
        TextView tv = (TextView) view.findViewById(R.id.tv_yuyan);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tvKnow.setOnClickListener(this);
        String aTime = mapTime.substring(11, 16);
        tvSucceed.setText(aTime);
        String str = mapTime.substring(0, 2);
        int i = Integer.parseInt(str);

        if (i < 12) {
            tvWorkType.setText("上");
            tv.setText("成功，从开始上班开始");
        } else {
            tvWorkType.setText("下");
            tv.setText("忙碌了一天了，好好休息吧");
            tvUpdataAttend.setVisibility(View.GONE);
            tvHorizontal.setVisibility(View.VISIBLE);
            llAfterIi.setVisibility(View.VISIBLE);
            rlAllCard.setVisibility(View.GONE);
            tvAttendanceTimeI.setText("打卡时间" + aTime);
        }
        setPopupWindow(mPopupWindow, view);

    }

    private void showFailPopupWindow() {

        View view = LayoutInflater.from(this).inflate(R.layout.fail_attendance, null);

        TextView tvSucceed = (TextView) view.findViewById(R.id.tv_succeed_time);
        TextView tvKnow = (TextView) view.findViewById(R.id.tv_know);
        TextView tvWorkType = (TextView) view.findViewById(R.id.tv_work_type);
        TextView tv = (TextView) view.findViewById(R.id.tv_yuyan);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tvKnow.setOnClickListener(this);
        String aTime = mapTime.substring(11, 16);
        tvSucceed.setText(aTime);
        String str = mapTime.substring(0, 2);
        int i = Integer.parseInt(str);

        if (i < 12) {
            tvWorkType.setText("上");
            tv.setText("成功，从开始上班开始");
        } else {
            tvWorkType.setText("下");
            tv.setText("忙碌了一天了，好好休息吧");
            tvUpdataAttend.setVisibility(View.GONE);
            tvHorizontal.setVisibility(View.VISIBLE);
            llAfterIi.setVisibility(View.VISIBLE);
            rlAllCard.setVisibility(View.GONE);
            tvAttendanceTimeI.setText("打卡时间" + aTime);
        }
        setPopupWindow(mPopupWindow, view);

    }

    private void setPopupWindow(PopupWindow popupWindow, View view) {
        popupWindow.setContentView(view);
        // 设置可以获得焦点
        popupWindow.setFocusable(true);
        // 设置弹窗内可点击
        popupWindow.setTouchable(true);
        // 设置弹窗外可点击
        popupWindow.setOutsideTouchable(true);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
//        允许点击外部消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ColorDrawable dw = new ColorDrawable(0x0000000);
        popupWindow.setBackgroundDrawable(dw);

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.7f;
        getWindow().setAttributes(attributes);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha = 1f;
                getWindow().setAttributes(attributes);
            }
        });
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ResultActivity.ACTION_SUCCEED:
                    showSucceedPopupWindow();
                    if (backAddress.equals(""))
                        tvAddressedI.setText(address);
                    else
                        tvAddressedI.setText(backAddress);
                    break;
                case ResultActivity.ACTION_FAILD:
                    showFailPopupWindow();
                    break;
            }
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，可以拍照
                    if (address == "") {
                        Toast.makeText(this, "没有获取到当前位置信息，请您重新定位", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    openCCActivity();
                } else {
                    Toast.makeText(this, "请您先开启相机权限！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocationValues();
                } else {
                    Toast.makeText(this, "请您先开启定位权限！", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
