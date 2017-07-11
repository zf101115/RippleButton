package com.zx.ripplebutton;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by root on 17-7-11.
 */

public class RippDrawable extends Drawable {
    private int mAlpha = 255;
    private int mRippColor = 0;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mRippPointX,mRippPointY;//圆心坐标
    private float mRippRadius = 0;//半径
    private int mWidth;//控件宽度
    private int defaultColor = 0x30ffffff;
    private boolean isActionUp;//手指抬起
    private boolean isEnterDone;//手指抬起

    public void setButtonWidth(int width){
        mWidth = width;
    }
    public RippDrawable(){
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖动
        setRippColor(defaultColor);
    }

    public void setRippColor(int color){
        mRippColor = color;
        onColorOrAlphaChange();
    }

    private void onColorOrAlphaChange(){
        mPaint.setColor(mRippColor);
        if (mAlpha!=255){
            int pAlpha = mPaint.getAlpha();
            int realAlpha = (int) (pAlpha*(mAlpha/255f));
            mPaint.setAlpha(realAlpha);
        }
        invalidateSelf();
    }

    public void onTouch(MotionEvent event){
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event.getX(),event.getY());
                break;

        }
        invalidateSelf();
    }

    private void onTouchCancel(float x, float y) {
    }

    private void onTouchUp(float x, float y) {
        isActionUp = true;
        if (isEnterDone){
            mRippRadius = 0;
            invalidateSelf();
        }
    }

    private void onTouchMove(float x, float y) {

    }

    private void onTouchDown(float x, float y) {
        isActionUp=false;
        isEnterDone=false;
        mDonePointX = x;
        mDonePointY = y;
        startEnterRunnable();
    }

    //进度值
    private float mProgress = 0;
    private android.view.animation.Interpolator mEnterInterploat = new DecelerateInterpolator(1);
    private Runnable mEventRunnable = new Runnable() {
        @Override
        public void run() {
            mProgress=mProgress+16f/mWidth;
            if (mProgress>1){
                if (isActionUp){
                    mRippRadius = 0;
                }
                isEnterDone=true;
                invalidateSelf();
                return;
            }
            float realyProsess = mEnterInterploat.getInterpolation(mProgress);
            onEnterProgress(realyProsess);
            //延迟16毫秒，保证界面刷新频率接近60FPS
            scheduleSelf(mEventRunnable, SystemClock.uptimeMillis()+16);
        }
    };

    private void onEnterProgress(float mProgress){
        mRippRadius = mWidth*mProgress;
        mRippPointX = getProgressValus(mDonePointX,mCenterPointX,mProgress);
        mRippPointY = getProgressValus(mDonePointY,mCenterPointY,mProgress);
        invalidateSelf();
    }

    private void startEnterRunnable(){
        mProgress = 0;
        unscheduleSelf(mEventRunnable);
        scheduleSelf(mEventRunnable, SystemClock.uptimeMillis());
    }


    private float getProgressValus(float start,float end,float progress){
        return start+(end-start)*progress;
    }
    //按下时点击的点
    private float mDonePointX,mDonePointY;
    //控件的中心区域
    private float mCenterPointX,mCenterPointY;


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mCenterPointX = bounds.centerX();
        mCenterPointY = bounds.centerY();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(mRippPointX,mRippPointY,mRippRadius,mPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

        mAlpha = alpha;
        onColorOrAlphaChange();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {//颜色滤镜

        if (mPaint.getColorFilter()!=colorFilter){
            mPaint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        int alpha = mPaint.getAlpha();
        if (alpha==255)
            return PixelFormat.OPAQUE;
        else if (alpha==0)
            return PixelFormat.TRANSPARENT;
        else
            return PixelFormat.TRANSLUCENT;

    }
}
