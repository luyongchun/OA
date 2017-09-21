package com.luyc.bnd.oaattendnace.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.luyc.bnd.oaattendnace.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/8/28.
 */

public class ProcessView extends View {
    /**
     * 默认线宽度
     */
    private static final float D_LINE_WIDTH = 3;
    /**
     * 默认滑动圆点半径
     */
    private static final float D_THUMB_RADIUS = 10;
    /**
     * 默认textsize
     */
    private static final float D_TEXT_SIZE = 13f;
    private static final int D_REACH_COLOR = 0xFFF1AE0D;
    private static final int D_UNREACH_COLOR = Color.WHITE;
    private static final int D_TEXT_COLOR = Color.WHITE;
    private static final String TAG = "ProcessView";

    private Paint linePaint;
    private TextPaint textPaint;
    private Paint thumbPaint;

    private float mTextSize = xx2px(TypedValue.COMPLEX_UNIT_SP, D_TEXT_SIZE);
    private float mLineWidth = xx2px(TypedValue.COMPLEX_UNIT_DIP, D_LINE_WIDTH);
    private float mThumbRadius = xx2px(TypedValue.COMPLEX_UNIT_DIP, D_THUMB_RADIUS);
    private int mReachedColor = D_REACH_COLOR;
    private int mUnreachedColor = D_UNREACH_COLOR;
    private int mTextColor = D_TEXT_COLOR;

    //当前进度
    private float mProgress = 0.0f;
    //所有的状态文字
    private String[] texts;
    private ArrayList<List<String[]>> mList = new ArrayList<>();
    private ArrayList<String[]> list = new ArrayList<>();
    private Bitmap bitmap;
    private String[] userNmes, userDepartment, userState;
//    private String[] userDepartment;
//    private String[] state;

    public ProcessView(Context context) {
        this(context, null);
    }

    public ProcessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(context, attrs, defStyleAttr);
        initViews();
    }

    public void setApprovalProcessText(String[] userNmes, String[] userDepartment, String[] userState) {
        this.userNmes = userNmes;
        this.userDepartment = userDepartment;
        this.userState = userState;
        Log.e(TAG, "userNmes: " + userNmes[1]);
    }

    /**
     * 获取我们的自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void obtainStyledAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProcessView, defStyleAttr, 0);
        texts = a.hasValue(R.styleable.ProcessView_texts) ?
                getResources().getStringArray(a.getResourceId(R.styleable.ProcessView_texts, 0)) : texts;
        mLineWidth = a.hasValue(R.styleable.ProcessView_line_width) ?
                a.getDimensionPixelSize(R.styleable.ProcessView_line_width, 0) : mLineWidth;
        mThumbRadius = a.hasValue(R.styleable.ProcessView_thumb_radius) ?
                a.getDimensionPixelSize(R.styleable.ProcessView_thumb_radius, 0) : mThumbRadius;
        mTextSize = a.hasValue(R.styleable.ProcessView_textsize) ?
                a.getDimensionPixelSize(R.styleable.ProcessView_text_color, 0) : mTextSize;
        mReachedColor = a.hasValue(R.styleable.ProcessView_color_reached) ?
                a.getColor(R.styleable.ProcessView_color_reached, D_REACH_COLOR) : D_REACH_COLOR;
        mUnreachedColor = a.hasValue(R.styleable.ProcessView_color_unreached) ?
                a.getColor(R.styleable.ProcessView_color_unreached, D_UNREACH_COLOR) : D_UNREACH_COLOR;
        mTextColor = a.hasValue(R.styleable.ProcessView_text_color) ?
                a.getColor(R.styleable.ProcessView_text_color, D_TEXT_COLOR) : D_TEXT_COLOR;
        a.recycle();
    }

    /**
     * 初始化一些对象
     */
    private void initViews() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        thumbPaint = new Paint(linePaint);

        textPaint.setTextSize(50);
        textPaint.setColor(mTextColor);
        linePaint.setStrokeWidth(mLineWidth);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightM = MeasureSpec.getMode(heightMeasureSpec);
        int defaultW = MeasureSpec.getSize(widthMeasureSpec);
        int defaultH = MeasureSpec.getSize(heightMeasureSpec);
        int resultW, resultH;
        resultW = defaultW;
        resultH = getDefaultHeight(defaultH, heightM);
        setMeasuredDimension(resultW, resultH);
    }

    private int getDefaultHeight(int height, int mode) {
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = height;
        } else {
            //获取文字的高度
            float textH = (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);
            //高度＝圆半径＋2.2＊线条宽度（也就是竖线高度）＋文字高度＊1.3（也就是空隙高度）＋0.5*文字高度
            result = (int) (mThumbRadius + mLineWidth * 2.2f + textH * 1.3f + 0.5 * textH);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画底部的竖线跟文字
        drawFoot(canvas);
        //画移动的小圆点跟进度条
        drawProgressAndThumb(canvas);
    }

    /**
     * 画底部的竖线跟文字
     */
    private void drawFoot(Canvas canvas) {
        //设置底部竖线宽度（底部的竖线会比进度条的要小一点）
        float lineWidth = mLineWidth * 0.8f;
        linePaint.setStrokeWidth(mLineWidth * 0.8f);
        //起始位置（也就是"订单已提交"的"已"字位置）
        int textHight = (int) (textPaint.descent() - textPaint.ascent());

        float startX = textPaint.measureText(texts[0]) / 2;
//        float startX = mThumbRadius+textHight;
        //结束的文字的位置（"已送达"的"送"字位置）
        float endTextW = textPaint.measureText(texts[texts.length - 1]) / 2;
        //绘制的终点位置
        float endX = getMeasuredWidth() - endTextW;
        //线条的总长度
        float lineW = (endX - startX) / (texts.length - 1);
        //竖线的高度
        float lineH = mLineWidth * 2.2f;
        //竖线的终点位置
        float lineY = mThumbRadius + mLineWidth / 2;
        //循环画出竖线跟文字
        for (int i = 0; i < texts.length; i++) {
            canvas.save();
            //每画一条竖线让画布向下平移linew个宽度
            canvas.translate(0, i * lineW);
            //如果当前进度>竖线所在的位置，就改变竖线的颜色
            linePaint.setColor(i * lineW >= mProgress * (endX - startX) ? mUnreachedColor : mReachedColor);
            float endX2 = i == texts.length - 1 ? startX - lineWidth / 2 : startX + lineWidth / 2;
            //绘画小短线
            if (i == (texts.length - 1)) {
                canvas.drawLine(lineY - 5, endX2, lineY + lineH, endX2 + 10, linePaint);
            } else if (i == 0) {
                canvas.drawLine(lineY - 5, endX2, lineY + lineH, endX2 - 10, linePaint);
            } else {
                canvas.drawLine(lineY - 5, endX2, lineY + lineH, endX2, linePaint);
            }
            //画文字
            textPaint.setTextAlign(Paint.Align.CENTER);
            float textH = (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);

            BitmapDrawable drawable = (BitmapDrawable) getContext().getResources().getDrawable(R.mipmap.to_go_out_icon);
            if (drawable != null) {
                bitmap = drawable.getBitmap();
            }
            canvas.drawBitmap(bitmap, endX2 - endX2 / 4, lineY + lineH + textH / 5, null);
            int width = bitmap.getWidth();

//            if (i==0){
//                canvas.drawText(userNmes[i], 2.5f * width + endX2, lineY + lineH + textH * 1f, textPaint);//姓名
//            }else {
            if (userNmes.length>0){
                canvas.drawText(userNmes[i], 2.5f * width + endX2, lineY + lineH + textH * 0.5f, textPaint);//姓名

                canvas.drawText(userDepartment[i], 2.5f * width + endX2, textH + (lineY + lineH + textH * 0.5f), textPaint);//部门
                canvas.drawText(userState[i], getWidth() - endX2 - 10, lineY + lineH + textH * 1.3f, textPaint);//是否审批
                canvas.restore();
            }

        }
    }

    private void drawProgressAndThumb(Canvas canvas) {
        float startY = textPaint.measureText(texts[0]) / 2;
        float endTextW = textPaint.measureText(texts[texts.length - 1]) / 2;
        float endX = getMeasuredHeight() - endTextW;


        //线条的总长度
        float lineW = (endX - startY) / (texts.length - 1);

        float lineX = mThumbRadius;

        linePaint.setStrokeWidth(mLineWidth);
        //draw basic line
        linePaint.setColor(mUnreachedColor);
        canvas.drawLine(lineX, startY, lineX, endX - 120, linePaint);
        //draw progress line
        float progressX = startY + (endX - 2 * startY) * mProgress;

        Log.e(TAG, "drawProgressAndThumb:  progressX-progressX/50" + (progressX - progressX / 50));
        linePaint.setColor(mReachedColor);

        canvas.drawLine(lineX, startY, lineX, progressX - progressX / 50, linePaint);
        //给移动圆点一个RadialGradient颜色梯度效果
        thumbPaint.setShader(new RadialGradient(progressX, mThumbRadius, mThumbRadius, new int[]{D_REACH_COLOR, Color.YELLOW}, null, Shader.TileMode.REPEAT));
        canvas.drawCircle(mThumbRadius, progressX - progressX / 35, mThumbRadius, thumbPaint);
    }

    public void setProgress(float progress) {
        if (progress != mProgress) {
            mProgress = progress;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                invalidate();
            } else {
                postInvalidate();
            }
        }
    }

    private float xx2px(int unit, float value) {
        Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        return (TypedValue.applyDimension(
                unit, value, r.getDisplayMetrics()));
    }


}
