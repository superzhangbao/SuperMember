package com.xishitong.supermember.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.zhpan.bannerview.constants.IndicatorSlideMode;
import com.zhpan.bannerview.indicator.BaseIndicatorView;

/**
 * author : zhangbao
 * date : 2020-02-11 14:31
 * description :
 */
public class OvalIndicatorView extends BaseIndicatorView {
    private float sliderHeight;
    private float maxWidth;
    private float minWidth;

    public OvalIndicatorView(Context context) {
        this(context, null);
    }

    public OvalIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OvalIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(normalColor);
        sliderHeight = normalIndicatorWidth / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxWidth = Math.max(normalIndicatorWidth, checkedIndicatorWidth);
        minWidth = Math.min(normalIndicatorWidth, checkedIndicatorWidth);
        setMeasuredDimension((int) ((pageSize - 1) * indicatorGap + maxWidth + (pageSize - 1) * minWidth),
                (int) (sliderHeight));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pageSize > 1) {
            for (int i = 0; i < pageSize; i++) {
                if (slideMode == IndicatorSlideMode.SMOOTH) {
                    smoothSlide(canvas, i);
                } else {
                    normalSlide(canvas, i);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void normalSlide(Canvas canvas, int i) {
        if (normalIndicatorWidth == checkedIndicatorWidth) {
            mPaint.setColor(normalColor);
            float left = i * (normalIndicatorWidth) + i * +indicatorGap;
            canvas.drawRoundRect(left, 0, left + normalIndicatorWidth, sliderHeight, 100.f, 100.f, mPaint);
            drawSliderStyle(canvas);
        } else {  //  仿支付宝首页轮播图的Indicator
            if (i < currentPosition) {
                mPaint.setColor(normalColor);
                float left = i * minWidth + i * indicatorGap;
                canvas.drawCircle(left + minWidth / 2, minWidth / 2, minWidth / 2, mPaint);
            } else if (i == currentPosition) {
                mPaint.setColor(checkedColor);
                float left = i * minWidth + i * indicatorGap;
                canvas.drawRoundRect(left, 0, (left + minWidth + (maxWidth - minWidth)), sliderHeight, 100.f, 100.f, mPaint);
            } else {
                mPaint.setColor(normalColor);
                float left = i * minWidth + i * indicatorGap + (maxWidth - minWidth);
                canvas.drawCircle(left + minWidth / 2, minWidth / 2, minWidth / 2, mPaint);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void smoothSlide(Canvas canvas, int i) {
        mPaint.setColor(normalColor);
        float left = i * (maxWidth) + i * +indicatorGap + (maxWidth - minWidth);
        canvas.drawRoundRect(left, 0, left + minWidth, sliderHeight, 100.0f, 100.0f, mPaint);
        drawSliderStyle(canvas);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
    }

    private void drawSliderStyle(Canvas canvas) {
        mPaint.setColor(checkedColor);
        float left = currentPosition * (maxWidth) + currentPosition * +indicatorGap + (maxWidth + indicatorGap) * slideProgress;
        canvas.drawRect(left, 0, left + maxWidth, sliderHeight, mPaint);
    }

    public OvalIndicatorView setSliderHeight(int sliderHeight) {
        this.sliderHeight = sliderHeight;
        return this;
    }

}
