package com.luyc.bnd.oaattendnace.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.luyc.bnd.oaattendnace.R;
import com.luyc.bnd.oaattendnace.utils.MyToos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements
        AMapLocationListener, LocationSource {

    @InjectView(R.id.tv_address)
    TextView tvAddress;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.tv_attendance)
    TextView tvAttendance;
    @InjectView(R.id.tv_again_location)
    TextView tvAgainLocation;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @InjectView(R.id.activity_map)
    LinearLayout activityMap;
    @InjectView(R.id.tv_in_attendance)
    TextView tvInAttendance;
    @InjectView(R.id.tv_nor_attendance)
    TextView tvNorAttendance;
    @InjectView(R.id.et_remarks)
    EditText etRemarks;
    @InjectView(R.id.ll_attendance)
    LinearLayout llAttendance;
    @InjectView(R.id.mapview)
    TextureMapView mapview;
    private UiSettings mUiSettings;
    private AMap aMap;
    private OnLocationChangedListener mListener = null;
    //    private TextureMapView mv;
    private String nowTime, address;
    private String mapTime;
    private Circle circle;
    // 标识首次定位
    private boolean isFirstLocation = true;

    private String TAG = "MapActivity";
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {


        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //可在其中解析amapLocation获取相应内容。
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    initMapData(aMapLocation);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    private void initMapData(AMapLocation aMapLocation) {
        address = aMapLocation.getAddress();
        if (circle != null) {
            circle.remove();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA);
        mapTime = sdf.format(new Date(aMapLocation.getTime()));//获取地图时间
        String street = aMapLocation.getStreet();//街道
        String streetNum = aMapLocation.getStreetNum();//街道号
        double latitude = aMapLocation.getLatitude();//经度
        double longitude = aMapLocation.getLongitude();//纬度
        LatLng start = new LatLng(22.811404, 113.33247);//考勤经纬度
        LatLng end = new LatLng(latitude, longitude);//定位的经纬度
        mListener.onLocationChanged(aMapLocation);//定位
        //以考勤地点经纬度做以250M半径画圆
        circle = aMap.addCircle(new CircleOptions()
                .center(start)
                .radius(300)
                .fillColor(Color.argb(1, 0, 0, R.color.color_iii))
                .strokeColor(Color.argb(1, 1, 1, R.color.color_iii))
                .strokeWidth(1));
        if (isFirstLocation) {
            // 设置缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            // 将地图移动到定位点
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(end));
            // 点击定位按钮 能够将地图的中心移动到定位点
            mListener.onLocationChanged(aMapLocation);
            isFirstLocation = false;
        }
        tvAddress.setText(address);
        Intent intent = new Intent();
        String mAddress = street + streetNum;
        intent.putExtra("mAddress", mAddress);
        intent.putExtra("address", address);
        MapActivity.this.setResult(0, intent);

        String mHour = nowTime.substring(0, 2);
        String mMin = nowTime.substring(3, 5);
        int Hour = Integer.parseInt(mHour);
        int Min = Integer.parseInt(mMin);
        float width = AMapUtils.calculateLineDistance(start, end);//比较两点之间的距离
        if (Hour <= 8 && Min <= 30 || Hour >= 17 && Min >= 30) {
            setNormalAttendance(width, "正常打卡", "正常", R.drawable.shape_attend_nor_bg);
        } else if (Hour == 8 && Min > 30 || Hour > 8 && Hour <= 12) {
            setLateAttdence(width, "迟到打卡", "迟到");
        } else if (Hour > 12 && Hour < 17 || Hour == 17 && Min < 30) {
            setLeaveEarlyAttdence(width, "早退打卡", "早退");
        }
    }

    //早退
    private void setLeaveEarlyAttdence(float width, String leaveEarly, String label) {
        if (width <= 400) {
            tvInAttendance.setText("(在");
            tvAttendance.setText(leaveEarly);
            tvNorAttendance.setText(label);
            Drawable drawable = getResources().getDrawable(R.drawable.shape_attend_late_bg);
            tvNorAttendance.setBackgroundDrawable(drawable);
            llAttendance.setBackgroundDrawable(drawable);
        } else {
            tvInAttendance.setText("(不在");
            tvAttendance.setText("外勤打卡");
            tvNorAttendance.setText("外勤");
            tvNorAttendance.setText(label);
            Drawable drawable = getResources().getDrawable(R.drawable.shape_attend_late_bg);
            tvNorAttendance.setBackgroundDrawable(drawable);
            llAttendance.setBackgroundDrawable(drawable);
        }
    }

    //迟到
    private void setLateAttdence(float width, String late, String label) {
        if (width <= 300) {
            tvInAttendance.setText("(在");
            tvAttendance.setText(late);
            tvNorAttendance.setText(label);
            Drawable drawable = getResources().getDrawable(R.drawable.shape_attend_late_bg);
            tvNorAttendance.setBackgroundDrawable(drawable);
            llAttendance.setBackgroundDrawable(drawable);
        } else {
            tvInAttendance.setText("(不在");
            tvAttendance.setText("外勤打卡");
            tvNorAttendance.setText(label);
            Drawable drawable = getResources().getDrawable(R.drawable.shape_attend_late_bg);
            tvNorAttendance.setBackgroundDrawable(drawable);
            llAttendance.setBackgroundDrawable(drawable);
        }
    }

    //正常
    private void setNormalAttendance(float width, String normal, String label, int shape_attend_nor_bg) {
        if (width <= 300) {
            tvInAttendance.setText("(在");
            tvAttendance.setText(normal);
            tvNorAttendance.setText(label);
            Drawable drawable = getResources().getDrawable(shape_attend_nor_bg);
            tvNorAttendance.setBackgroundDrawable(drawable);
            llAttendance.setBackgroundDrawable(drawable);
        } else {
            tvInAttendance.setText("(不在");
            tvAttendance.setText("外勤打卡");
            tvNorAttendance.setText(label);
            Drawable drawable = getResources().getDrawable(R.drawable.shape_attend_late_bg);
            tvNorAttendance.setBackgroundDrawable(drawable);
            llAttendance.setBackgroundDrawable(drawable);
        }
    }

    //打开相机
    private void openCCActivity() {
        if (address == null) {
            Toast.makeText(this, "当前获取到的地址为空，请您重新定位", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, CustomerCameraActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("mapTime", mapTime);
        bundle.putString("address", address);
        Log.e(TAG, "setPermision: address" + address);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }

    //定时器
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
                    tvTime.setText(sysTimeStr);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.inject(this);
        getSystemTime();
        new TimeThread().start();//开启定时器
        initMap();
        setStyle(savedInstanceState);
        set();
    }

    private void getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        nowTime = sdf.format(new Date());
        tvTime.setText(nowTime);
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

    private void initMap() {
//        mv = ((TextureMapView) findViewById(R.id.mapview));
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();//重新绘制加载地图
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停地图的绘制
        mapview.onPause();
        aMap.clear();
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();//销毁地图
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    private void setPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            if (address == null) {
                Toast.makeText(this, "没有获取到当前位置信息，请您重新定位", Toast.LENGTH_SHORT).show();
                return;
            }
            openCCActivity();
        }
    }

    private void set() {
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);//是否能设置缩放级别
        mUiSettings.setCompassEnabled(true);//指南针
        mUiSettings.setScaleControlsEnabled(true);//
        mUiSettings.setRotateGesturesEnabled(false);
    }

    private void setStyle(Bundle savedInstanceState) {
        mapview.onCreate(savedInstanceState);//创建地图
        if (aMap == null) {
            aMap = mapview.getMap();//显示地图
        }
        //自定义图标
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location_i));
        aMap.setMyLocationStyle(myLocationStyle);
        //设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.setMyLocationEnabled(true);
        /*
         *设置默认定位按钮是否显示，非必需设置。
         */
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点
        aMap.setMyLocationEnabled(false);
        //设置定位资源。如果不设置此定位资源则定位按钮不可点击。
        aMap.setLocationSource(this);
        // 显示实时交通状况
        aMap.setTrafficEnabled(true);
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        //设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    private void requestLocationAdrees(int requestType) {
        if (requestType != -1) {
            mLocationClient = new AMapLocationClient(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
        }
        if (MyToos.isLOLLIPOP()) {
            if (ContextCompat.checkSelfPermission(MapActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                initLocationValues();
            }
        } else {
            initLocationValues();
        }
    }

    private void initLocationValues() {

        mLocationClient.setLocationListener(mLocationListener);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。Device_Sensors设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        // 设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟软件Mock位置结果，多为模拟GPS定位结果，默认为false，不允许模拟位置。
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        // 设置是否开启定位缓存机制
        // 缓存机制默认开启，可以通过以下接口进行关闭。
        // 当开启定位缓存功能，在高精度模式和低功耗模式下进行的网络定位结果均会生成本地缓存，不区分单次定位还是连续定位。GPS定位结果不会被缓存。
        // 关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    @OnClick({R.id.tv_again_location, R.id.ll_attendance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_attendance:
                setPermision();
                break;
            case R.id.tv_again_location:
                requestLocationAdrees(1);
                isFirstLocation = true;
                break;

        }

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && mListener != null) {
            mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
        } else {
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        requestLocationAdrees(-1);
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCCActivity();
                } else {
                    Toast.makeText(this, "请您先开启照相机权限！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
