package com.exalpme.bozhilun.android.siswatch;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.exalpme.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.exalpme.bozhilun.android.bean.MessageEvent;
import com.exalpme.bozhilun.android.bleutil.Customdata;
import com.exalpme.bozhilun.android.siswatch.utils.WatchUtils;
import com.exalpme.bozhilun.android.siswatch.utils.test.TimeInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/26.
 */

public class GetWatchTimeActivity extends WatchBaseActivity implements TimeInterface {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.showWatchTimeTv)
    TextView showWatchTimeTv;
    @BindView(R.id.showAnalysisTimeTv)
    TextView showAnalysisTimeTv;
    @BindView(R.id.showSysTimeTv)
    TextView showSysTimeTv;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    showSysTimeTv.setText(sdf.format(new Date(System.currentTimeMillis())));
                    break;
            }
        }
    };
    @BindView(R.id.searchWatchBtn)
    Button searchWatchBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_watchtime);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new MessageEvent("getWatchTime"));

        initViews();
        new TimeThread().start();   //获取当前系统时间
        MyApp.getWatchBluetoothService().setTimeInterface(this);


    }

    @Override
    public void getWatchTime(Object o) {
        Log.e("GETQW", "------jiekou------" + Arrays.toString((byte[]) o));
    }

    @OnClick(R.id.searchWatchBtn)
    public void onViewClicked() {
        EventBus.getDefault().post(new MessageEvent("laidianphone"));
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = 1001;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private void initViews() {
        tvTitle.setText("获取手表的时间");
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String result = event.getMessage();
        if (!WatchUtils.isEmpty(result)) {
            Log.e("GETWATCH", "-----result-----" + result);
            if (result.equals("rebackWatchTime")) {
                byte[] watchTimeData = (byte[]) event.getObject();
                showWatchTimeTv.setText(Customdata.bytes2HexString(watchTimeData) + "-" + Arrays.toString(watchTimeData));
                for (int i = 0; i < watchTimeData.length; i++) {

                }
                showAnalysisTimeTv.setText(String.valueOf(20) + watchTimeData[6] + "-" + watchTimeData[7] + "-" + watchTimeData[8] + " " + watchTimeData[9] + ":" + watchTimeData[10] + ":" + watchTimeData[11]);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
