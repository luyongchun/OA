package com.luyc.bnd.oaattendnace.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/9/15.
 */

public class MyTextCircleView extends View {
    private Paint outPaint;
    private Paint mPaint;
    private int position;
    private String text="";
    private Paint tPaint;
    private int ARC_LINE_LENGTH;
    private int ARC_LINE_WIDTH;
    //进度条所占用的角度
    private static final int ARC_FULL_DEGREE =360;
    //进度条个数
    private static final int COUNT =100;
    //每个进度条所占用的角度
    private static final float ARC_EACH_PROGRESS = ARC_FULL_DEGREE *1.0f/(COUNT -1);
    public MyTextCircleView(Context context) {
        this(context, null);
    }

    public MyTextCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();
        //绘制里外两个圆
        canvas.drawCircle(width / 2, height / 2, width / 3, outPaint);
        canvas.save();
        canvas.drawCircle(width / 2, height / 2, width / 5, mPaint);
        canvas.save();

        //绘制文字
        if (text.equals("")) {
            text = "Tu";
        }
        if (position %2==0){
            text = "Tu";
        }
        float measureText = tPaint.measureText(text);
        canvas.drawText(text, width / 2 - measureText / 2, height / 2 + measureText / 3.5f, tPaint);
        canvas.save();

    }

    public void setPaintStyle(int position, String text) {
        this.position = position;
        this.text = text;
        initPaint();
    }

    private void initPaint() {
        outPaint = new Paint();
        if (position%2==0) {
            outPaint.setColor(Color.RED);
        } else {
            outPaint.setColor(Color.GREEN);
        }
        outPaint.setStyle(Paint.Style.FILL);
        outPaint.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);

        tPaint = new Paint();
        tPaint.setColor(Color.WHITE);
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setAntiAlias(true);
        tPaint.setTextSize(40);

    }
}
