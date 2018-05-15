package com.exalpme.bozhilun.android.w30s.carema.news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.R;
import com.linj.FileOperateUtil;
import com.linj.album.view.FilterImageView;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraView;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewW30sCameraActivity extends AppCompatActivity implements CameraContainer.TakePictureListener {

    @BindView(R.id.my_container)
    CameraContainer myContainer;//相机取景框
    @BindView(R.id.btn_switch_camera)
    ImageView btnSwitchCamera;//切换摄像头
    @BindView(R.id.btn_flash_mode)
    ImageView btnFlashMode;//闪光灯
    @BindView(R.id.btn_shutter_camera)
    ImageButton btnShutterCamera;//拍照
    @BindView(R.id.btn_thumbnail)
    FilterImageView btnThumbnail;//图片
    @BindView(R.id.text_number_time)
    TextView textNumberTime;
    @BindView(R.id.rela_number_time)
    RelativeLayout relaNumberTime;
    @BindView(R.id.image_back_out)
    ImageView imageBackOut;
    private String mSaveRoot = "W30sPic";
    private int NumberTime = 5;
    private Runnable runnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//【旋转问题】首先强制竖屏，手机横过来时候 控件不变
        setContentView(R.layout.activity_new_w30s_camera);
        ButterKnife.bind(this);
        myContainer.setRootPath(mSaveRoot);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册连接状态的广播
        registerReceiver(myBroadcastReceiver, makeGattUpdateIntentFilter());
        initThumbnail();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
        }
    }

    /**
     * 广播过滤
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W30SBLEServices.ACTION_CAMERA_AVAILABLE_DEVICE);
        return intentFilter;
    }

    private void initThumbnail() {
        String thumbFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
        List<File> files = FileOperateUtil.listFiles(thumbFolder, ".jpg");
        if (files != null && files.size() > 0) {
            Bitmap thumbBitmap = BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
            if (thumbBitmap != null) {
                btnThumbnail.setImageBitmap(thumbBitmap);
            }
        } else {
            btnThumbnail.setImageBitmap(null);
        }
    }


    /**
     * 辅助拍照倒计时
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    relaNumberTime.setVisibility(View.GONE);
                    NumberTime = 5;
                    myContainer.takePicture(NewW30sCameraActivity.this);
                    handler.removeMessages(0x01);
                    handler.removeCallbacks(runnable);
                    break;
            }
            return false;
        }
    });


    boolean TakePictures = true;

    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!WatchUtils.isEmpty(intent.getAction()) && intent.getAction().equals(W30SBLEServices.ACTION_CAMERA_AVAILABLE_DEVICE)) {
                synchronized (this) {
                    if (TakePictures) {
                        TakePictures = false;
                        relaNumberTime.setVisibility(View.VISIBLE);
                        textNumberTime.setText(NumberTime + "");
                        uiClick(false);
                        timingDown();
                    }

                }

            }
        }
    };


    /**
     * 拍照点击是一些状态禁止操作
     *
     * @param statue
     */
    public void uiClick(boolean statue) {
        imageBackOut.setClickable(statue);
        imageBackOut.setEnabled(statue);

        btnSwitchCamera.setClickable(statue);
        btnSwitchCamera.setEnabled(statue);

        btnFlashMode.setClickable(statue);
        btnFlashMode.setEnabled(statue);

        btnShutterCamera.setClickable(statue);
        btnShutterCamera.setEnabled(statue);

        btnThumbnail.setClickable(statue);
        btnThumbnail.setEnabled(statue);
    }


    /**
     * 拍照倒计时
     */
    private void timingDown() {
        synchronized (this) {
            if (runnable != null) {
                runnable = null;
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        NumberTime--;
                        textNumberTime.setText(NumberTime + "");
                        if (NumberTime > 0) {
                            // 循环调用实现定时刷新界面
                            handler.postDelayed(this, 1000);
                        } else if (NumberTime <= 0) {
                            handler.sendEmptyMessage(0x01);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        }
    }


    @OnClick({R.id.btn_switch_camera, R.id.btn_flash_mode, R.id.btn_shutter_camera, R.id.btn_thumbnail, R.id.image_back_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_switch_camera://切换摄像头
                if (TakePictures) {
                    myContainer.switchCamera();
                }
                break;
            case R.id.btn_flash_mode://闪光灯
                if (TakePictures) {
                    if (myContainer.getFlashMode() == CameraView.FlashMode.ON) {
                        myContainer.setFlashMode(CameraView.FlashMode.OFF);
                        btnFlashMode.setImageResource(R.drawable.btn_flash_off);
                    } else if (myContainer.getFlashMode() == CameraView.FlashMode.OFF) {
                        myContainer.setFlashMode(CameraView.FlashMode.AUTO);
                        btnFlashMode.setImageResource(R.drawable.btn_flash_auto);
                    } else if (myContainer.getFlashMode() == CameraView.FlashMode.AUTO) {
                        myContainer.setFlashMode(CameraView.FlashMode.TORCH);
                        btnFlashMode.setImageResource(R.drawable.btn_flash_torch);
                    } else if (myContainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                        myContainer.setFlashMode(CameraView.FlashMode.ON);
                        btnFlashMode.setImageResource(R.drawable.btn_flash_on);
                    }
                }

                break;
            case R.id.btn_shutter_camera://拍照
                if (TakePictures) {
                    TakePictures = false;
                    relaNumberTime.setVisibility(View.VISIBLE);
                    textNumberTime.setText(NumberTime + "");
                    uiClick(false);
                    timingDown();
                }
                break;
            case R.id.btn_thumbnail://图片
                if (TakePictures) {
                    startActivity(new Intent(NewW30sCameraActivity.this, W30SAlbumAty.class));
                }
                break;
            case R.id.image_back_out:
                if (TakePictures) {
                    finish();
                }
                break;
        }
    }

    /**
     * @param bm 拍照生成的图片
     */
    @Override
    public void onTakePictureEnd(Bitmap bm) {
        TakePictures = true;
        uiClick(true);
    }

    /**
     * @param bm      拍照生成的图片
     * @param isVideo true：当前为录像缩略图 false:为拍照缩略图
     */
    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
        if (bm != null) {
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 213, 213);
            btnThumbnail.setImageBitmap(thumbnail);
        }
    }


}
