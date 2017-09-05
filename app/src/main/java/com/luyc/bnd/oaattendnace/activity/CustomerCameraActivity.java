package com.luyc.bnd.oaattendnace.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luyc.bnd.oaattendnace.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CustomerCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {


    @InjectView(R.id.sfv_camera)
    SurfaceView sfvCamera;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.tv_date)
    TextView tvDate;
    @InjectView(R.id.imgvBtn_switchFlash)
    ImageButton imgvBtnSwitchFlash;
    @InjectView(R.id.imgvBtn_switchCamera)
    ImageButton imgvBtnSwitchCamera;
    @InjectView(R.id.rl_setting)
    RelativeLayout rlSetting;
    @InjectView(R.id.tv_username)
    TextView tvUsername;
    @InjectView(R.id.tv_address)
    TextView tvAddress;
    @InjectView(R.id.btn_cancel)
    TextView btnCancel;
    @InjectView(R.id.iv_take_photo)
    ImageView ivTakePhoto;
    @InjectView(R.id.activity_customer_camera)
    RelativeLayout activityCustomerCamera;
    @InjectView(R.id.cb_light)
    CheckBox cbLight;
    @InjectView(R.id.btn_switch)
    Button btnSwitch;


    private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

    //声明一个camera对象
    private Camera camera;
    //图片的实时预览
    private SurfaceView suf_camera;
    private SurfaceHolder surfaceHolder;
    //相机参数设置
    private Camera.Parameters parameters;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    //    private int mCameraId;
    private String TAG = "CustomerCameraActivity";
    public byte[] datas;
    private String nowData;
    private String nowTime;
    private CameraManager manager;
    private boolean isCheched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_camera);
        ButterKnife.inject(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        nowData = now.substring(0, 11);
        tvDate.setText(nowData);
        nowTime = now.substring(11);
        tvTime.setText(nowTime);

        suf_camera = (SurfaceView) findViewById(R.id.sfv_camera);
        checkSoftStage();//检查是否有内存卡

        suf_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        camera.setOneShotPreviewCallback(null);
                    }
                });
            }
        });
        //设置为前置相机
        cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
        //设置为不可旋转相机
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //拍照过程屏幕一直处于高亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceHolder = suf_camera.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓存
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case 1:
                    setResult(RESULT_OK, data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void takePhoto() {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        //获取相机参数
        parameters = camera.getParameters();
        //设置照片格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //设置对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPictureSize(width, height - 110);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            datas = data;
                            if (isLOLLIPOP()) {
                                if (ContextCompat.checkSelfPermission(CustomerCameraActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(CustomerCameraActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                } else {
                                    dealWithCameraData(data);
                                }
                            } else {
                                dealWithCameraData(data);
                            }
                        }
                    });
                }

            }
        });
//        camera.takePicture();
    }

    private void dealWithCameraData(byte[] data) {
        FileOutputStream fos = null;
//        String filePath = "/sdcard/oa" + System.currentTimeMillis() + ".jpg";
        String filePath = "/sdcard/oa" + "temp.jpg";
        try {
            File tempFile = new File(filePath);
            if (tempFile.exists()){
                tempFile.delete();
                tempFile.createNewFile();
            }
            fos = new FileOutputStream(tempFile);
            fos.write(data); //保存图片数据
            fos.close();
            //启动显示图片的activity
            Intent intent = new Intent(CustomerCameraActivity.this, ResultActivity.class);
            intent.putExtra("filePath", filePath);
            intent.putExtra("nowData", nowData);
            intent.putExtra("nowTime", nowTime);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这是点击surfaceview聚焦所调用的方法
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                takePhoto();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dealWithCameraData(datas);
                } else {
                    Toast.makeText(CustomerCameraActivity.this, "您的权限未开启，请先开启读写内存权限", Toast.LENGTH_SHORT).show();
                }
               break;


        }
    }

    public void getCamera() {
        //获取相机实例
        if (camera == null)
            camera = Camera.open();
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();//停止预览
            camera.release();//释放相机
            camera = null;
            surfaceHolder = null;
        }

    }

    public void setPrive(Camera camera, SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            //开始预览
            camera.setDisplayOrientation(90);
            camera.startPreview();//打开设摄像头
        } catch (IOException e) {
            e.printStackTrace();
            releaseCamera();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        getCamera();
        if (surfaceHolder != null) {
            setPrive(camera, surfaceHolder);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setPrive(camera, surfaceHolder);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        setPrive(camera, surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @OnClick({R.id.btn_cancel, R.id.iv_take_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_take_photo:
                takePhoto();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.cb_light:
                manager = ((CameraManager) getSystemService(Context.CAMERA_SERVICE));
                selectLight();
                break;
            case R.id.btn_switch:
                switchCamera();
                break;
            default:
                break;
        }
    }

    private void selectLight() {
        if (isLOLLIPOP() && isCheched) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setTorchMode("1", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (isLOLLIPOP() && !isCheched) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setTorchMode("0", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void switchCamera() {
        //切换前后摄像头
        int cameraCount = 0;
        cameraCount = Camera.getNumberOfCameras();//获取摄像头个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            //获取每个摄像头的信息
            if (cameraPosition == 1) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;
                    camera = Camera.open(i);
                    try {
                        camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setPrive(camera,surfaceHolder);
                    cameraPosition = 0;
                    break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;
                    camera = Camera.open(i);
                    try {
                        camera.setPreviewDisplay(surfaceHolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setPrive(camera,surfaceHolder);

                    cameraPosition = 1;
                    break;
                }
            }
        }
    }
    /**
     * 检测手机是否存在SD卡,网络连接是否打开
     */
    private void checkSoftStage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //判断是否存在SD卡
            String rootPath = Environment.getExternalStorageDirectory().getPath();  //获取SD卡的根目录
            File file = new File(rootPath);
            if (!file.exists()) {
                file.mkdir();
            }
        } else {
            new AlertDialog.Builder(this).setMessage("检测到手机没有存储卡！请插入手机存储卡再开启本应用。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    /**
     * 判断Android系统版本是否 >= LOLLIPOP(API21)
     *
     * @return boolean
     */
    private boolean isLOLLIPOP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }
    }

}
