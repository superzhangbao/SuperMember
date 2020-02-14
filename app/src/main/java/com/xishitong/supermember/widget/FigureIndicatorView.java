package com.xishitong.supermember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.zhpan.bannerview.indicator.BaseIndicatorView;
import com.zhpan.bannerview.utils.BannerUtils;

/**
 * author : zhangbao
 * date : 2020-02-14 01:11
 * description :
 */
public class FigureIndicatorView extends BaseIndicatorView {

    private int radius = BannerUtils.dp2px(20);

    private int backgroundColor = Color.parseColor("#191919");

    private int textColor = Color.WHITE;

    private int textSize = BannerUtils.dp2px(13);

    private Paint mPaint;

    public FigureIndicatorView(Context context) {
        this(context, null);
    }

    public FigureIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FigureIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(2 * radius, 2 * radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pageSize > 1) {
            mPaint.setColor(backgroundColor);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
            mPaint.setColor(textColor);
            mPaint.setTextSize(textSize);
            String text = currentPosition+ 1 + "/" + pageSize;
            int textWidth = (int) mPaint.measureText(text);
            Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
            canvas.drawText(text, (getWidth() - textWidth) / 2, baseline, mPaint);
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
