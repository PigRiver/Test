package com.orient.test.animation;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ContentScaleAnimation extends Animation {
    private float mPivotX;// 锚点在parentView中x轴坐标
    private float mPivotY;// 锚点在parentView中y轴坐标
    private float mPivotXValue; // 控件左上角X, marginLeft
    private float mPivotYValue; // marginTop
    private final float scaleXTimes;
    private final float scaleYTimes;
    private boolean mReverse;

    public ContentScaleAnimation(float mPivotXValue, float mPivotYValue, float scaleXTimes, float scaleYTimes, boolean mReverse) {

        this.mPivotXValue = mPivotXValue;
        this.mPivotYValue = mPivotYValue;
        this.scaleXTimes = scaleXTimes;
        this.scaleYTimes = scaleYTimes;
        this.mReverse = mReverse;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        Matrix matrix=t.getMatrix();//缩放方法
        if (mReverse) {
            matrix.postScale(1 + (scaleXTimes - 1) * (1.0f - interpolatedTime), 1 + (scaleYTimes - 1) * (1.0f - interpolatedTime), mPivotX - mPivotXValue, mPivotY - mPivotYValue);
        } else {
            matrix.postScale(1 + (scaleXTimes - 1) * interpolatedTime, 1 + (scaleYTimes - 1) * interpolatedTime, mPivotX - mPivotXValue , mPivotY - mPivotYValue );
        }
    }
    //缩放点坐标值
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mPivotX = resolvePivotX(mPivotXValue, parentWidth, width);
        mPivotY = resolvePivoY(mPivotYValue, parentHeight, height);
    }
    //缩放点坐标值   缩放点到自身左边距离/缩放点到父控件左边的距离=缩放点自身右侧距离/缩放点到父控件右边的距离
    private float resolvePivotX(float marginLeft, int parentWidth, int width) {
        return (marginLeft * parentWidth) / (parentWidth - width);
    }

    private float resolvePivoY(float marginTop, int parentHeight, int height) {
        return (parentHeight * marginTop) / (parentHeight - height);
    }

    public void reverse() {
        mReverse = !mReverse;
    }

    public boolean getMReverse() {
        return mReverse;
    }
}
