package com.exalpme.bozhilun.android.w30s.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.suchengkeji.android.w30sblelibrary.bean.W30S_SleepData;
import com.suchengkeji.android.w30sblelibrary.bean.servicebean.W30S_SleepDataItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Features:
 * Founder: An
 * Create time: 2018/5/2.
 */
public class W30S_SleepChart extends View {
    private static final String TAG = "W30S_SleepChart";
    private int AD = 0;
    private int pos = 0;
    private Paint paint;
    private List<W30S_SleepDataItem> beanList;
    private float startX;


    public W30S_SleepChart(Context context) {
        super(context);
        intt();
        init();
    }

    public W30S_SleepChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        intt();
        init();
    }


    private void intt() {
        if (beanList != null) {
            beanList.clear();
            beanList = null;
        }
        if (getBeanList() == null) {
            beanList = new ArrayList<>();
        } else {
            beanList = getBeanList();
        }
    }


    public List<W30S_SleepDataItem> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<W30S_SleepDataItem> w30S_sleepDataItems) {
        if (this.beanList != null) {
            this.beanList.clear();
            this.beanList = null;
        }
        this.beanList = w30S_sleepDataItems;
    }


    public void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);//FILL_AND_STROKE  FILL
        paint.setStrokeWidth(0.1f);
        paint.setDither(true);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (beanList == null || beanList.size() <= 0) return;
        if (paint == null) init();
        AD = 0;
        pos = 0;
        for (int i = 0; i < beanList.size() - 1; i++) {
            String time = beanList.get(i).getStartTime();
            String time1 = beanList.get(i + 1).getStartTime();
            String[] split = time.split("[:]");
            String[] split1 = time1.split("[:]");
            int HH = Integer.valueOf(split[0]);
            int HH1 = Integer.valueOf(split1[0]);
            if (HH > HH1) {
                HH1 += 24;
            }
            Integer integer = HH1 - HH;
            int MM = Integer.valueOf(split1[1]) - Integer.valueOf(split[1]);
            AD += (integer * 60 + MM);
        }
        double it = (double) getWidth() / (double) AD;
        double v = startX * ((double) getWidth() / (double) AD);
        //Log.d("=======清醒", AD + "-------" + it);
        for (int i = 0; i < beanList.size(); i++) {
            String time = null;
            String time1 = null;
            String type = null;
            if (i >= (beanList.size() - 1)) {
                time = beanList.get(i).getStartTime();
                time1 = beanList.get(i).getStartTime();
                type = beanList.get(i).getSleep_type();
            } else {
                time = beanList.get(i).getStartTime();
                time1 = beanList.get(i + 1).getStartTime();
                type = beanList.get(i).getSleep_type();
            }

            String[] split = time.split("[:]");
            String[] split1 = time1.split("[:]");

            int HH = Integer.valueOf(split[0]);
            int HH1 = Integer.valueOf(split1[0]);
            if (HH > HH1) {
                HH1 += 24;
            }
            Integer integer = HH1 - HH;
            int MM = Integer.valueOf(split1[1]) - Integer.valueOf(split[1]);
            int all = (integer * 60 + MM);
            double paintWidth = ((double) all * it);
//            int paintWidth = (int) ((double) all / (double) AD * getWidth());
            if (v >= pos && v < (pos + paintWidth)) {
                if (v > 0) {
                    if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
                        //Log.d("=====AAA==清醒", time + "-------" + time1);
                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
                    } else if (type.equals("2")) {  //潜睡状态
                        //Log.d("=====AAA==浅睡", time + "-------" + time1);
                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
                    } else if (type.equals("3")) {  //深睡
                        //Log.d("=====AAA==深睡", time + "-------" + time1);
                        mDataTypeListenter.OnDataTypeListenter(type, time, time1);
                    }
                }
            }

            if (type.equals("0") || type.equals("4") || type.equals("1") || type.equals("5")) { //清醒状态
                paint.setColor(Color.parseColor("#fcd647"));
                Rect rectF1 = new Rect(pos, 0, (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF1, paint);
                pos = (int) (pos + paintWidth);
                //Log.d("=====BBB==清醒", time + "-------" + time1);
            } else if (type.equals("2")) {  //潜睡状态
                paint.setColor(Color.parseColor("#a6a8ff"));
                Rect rectF2 = new Rect(pos, 0, (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF2, paint);
                pos = (int) (pos + paintWidth);
                //Log.d("=====BBB==浅睡", time + "-------" + time1);
            } else if (type.equals("3")) {  //深睡
                paint.setColor(Color.parseColor("#b592d6"));
                Rect rectF3 = new Rect(pos, 0, (int) (pos + paintWidth), getHeight());
                canvas.drawRect(rectF3, paint);
                pos = (int) (pos + paintWidth);
                //Log.d("=====BBB==深睡", time + "-------" + time1);
            }

        }
        paint.setColor(Color.WHITE);
        Rect rectM = new Rect((int) v - 2, 20, (int) v + 2, getHeight());
        canvas.drawRect(rectM, paint);
    }


    public void setSeekBar(float p) {
        if (p >= 0) {
            startX = p;
            invalidate();
        }
    }


    private DataTypeListenter mDataTypeListenter;

    public void setmDataTypeListenter(DataTypeListenter mDataTypeListenter) {
        this.mDataTypeListenter = mDataTypeListenter;
    }

    public DataTypeListenter getmDataTypeListenter() {
        return mDataTypeListenter;
    }

    public interface DataTypeListenter {
        void OnDataTypeListenter(String statue, String startTime, String endTime);
    }

}
