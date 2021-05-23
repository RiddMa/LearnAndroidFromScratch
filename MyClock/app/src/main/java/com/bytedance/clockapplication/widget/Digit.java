package com.bytedance.clockapplication.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Digit extends View {

    private final Handler handler = new Handler();
    private Paint mDigitPaint;

    private int mWidth, mCenterX, mCenterY, mRadius;

    public Digit(Context context) {
        super(context);
        init(context);
    }

    public Digit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Digit(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Digit(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context) {
        mDigitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDigitPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDigitPaint.setStrokeCap(Paint.Cap.ROUND);

        mDigitPaint.setColor(Color.WHITE);
        mDigitPaint.setTextSize(100);

        // update();
    }

    private void update() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate(); // 重新绘制
            }
        }, 50, 50);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        drawDigit(canvas);

        handler.postDelayed(() -> postInvalidate(), 50);
    }

    private void drawDigit(Canvas canvas) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String s = df.format(new Date());

        Rect rect = new Rect(); //获取数字的宽度和高度
        mDigitPaint.getTextBounds(s, 0, s.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        canvas.drawText(s, (float) (mCenterX - textWidth / 2), (float) (mCenterY - textHeight / 2), mDigitPaint);
    }
}
