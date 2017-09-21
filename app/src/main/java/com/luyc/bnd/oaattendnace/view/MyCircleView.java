package com.luyc.bnd.oaattendnace.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.luyc.bnd.oaattendnace.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by admin on 2017/9/2.
 */

public class MyCircleView extends View {
    private Paint nPaint;
    private Paint wPaint;

    private int paintColorType = 0;

    public MyCircleView(Context context) {
        this(context, null);
    }

    public MyCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3, wPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 3.3f, nPaint);
        invalidate();
    }

    public void setPaintColor(int paintColorType) {
        this.paintColorType = paintColorType;
        initPaint();
        invalidate();
    }

    private void initPaint() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        String mHour = now.substring(0, 2);
        String mMin = now.substring(3);

        int Hour = Integer.parseInt(mHour);
        int Min = Integer.parseInt(mMin);
        if (wPaint == null) {
            wPaint = new Paint();
        }
        wPaint.setAntiAlias(true);
        wPaint.setDither(true);
        wPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (nPaint == null) {
            nPaint = new Paint();
        }
        nPaint.setAntiAlias(true);
        nPaint.setDither(true);
        nPaint.setStyle(Paint.Style.FILL);

        wPaint.setColor(getResources().getColor(R.color.color_iiis));
        nPaint.setColor(getResources().getColor(R.color.color_iii));
//        Hour=7;
        //迟到、早退或是上班时间都为橙色

        if (paintColorType == 1) {
            if (Hour == 8 && Min > 30 || Hour > 8 && Hour < 12
                    || Hour > 12 && Hour < 17 || Hour == 17 && Min < 30) {
                wPaint.setColor(getResources().getColor(R.color.color_iis));
                nPaint.setColor(getResources().getColor(R.color.color_ii));
            } else {
                wPaint.setColor(getResources().getColor(R.color.color_iiis));
                nPaint.setColor(getResources().getColor(R.color.color_iii));
            }
        } else if (paintColorType == -1) {//不在考勤范围为红色
            wPaint.setColor(getResources().getColor(R.color.color_laters));
            nPaint.setColor(getResources().getColor(R.color.color_later));
        }
    }


}



