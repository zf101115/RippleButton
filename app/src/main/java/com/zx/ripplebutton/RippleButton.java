package com.zx.ripplebutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by root on 17-7-11.
 */

public class RippleButton extends Button {
    private RippDrawable mRippDrawable;
    public RippleButton(Context context) {
        this(context,null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippDrawable = new RippDrawable();
        //设置刷新接口，view中已经实现
        mRippDrawable.setCallback(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置drawable绘制与刷新的区域
        mRippDrawable.setButtonWidth(w/2);
        mRippDrawable.setBounds(0,0,getWidth(),getHeight());
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        //验证drawable是否OK
        return who==mRippDrawable||super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRippDrawable.draw(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRippDrawable.onTouch(event);
        super.onTouchEvent(event);
        return true;
    }
}
