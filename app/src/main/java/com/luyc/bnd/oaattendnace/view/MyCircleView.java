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

    public MyCircleView(Context context) {
        this(context,null);
    }

    public MyCircleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/3,wPaint);
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/3.5f,nPaint);

    }

    private void initPaint() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.CHINA);
        String now = sdf.format(new Date());
        int parseTime = Integer.parseInt(now);
        wPaint = new Paint();
        wPaint.setAntiAlias(true);
        wPaint.setDither(true);
        wPaint.setStyle(Paint.Style.STROKE);

        nPaint = new Paint();
        nPaint.setAntiAlias(true);
        nPaint.setDither(true);
        nPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (parseTime>12){
            wPaint.setColor(getResources().getColor(R.color.color_ii));
            nPaint.setColor(getResources().getColor(R.color.color_ii));
        }else {
            wPaint.setColor(getResources().getColor(R.color.color_iii));
            nPaint.setColor(getResources().getColor(R.color.color_iii));
        }
    }


}
