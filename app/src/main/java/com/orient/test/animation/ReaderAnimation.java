package com.orient.test.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orient.test.R;

public class ReaderAnimation {
    private Context mContext;
    private ContentScaleAnimation mScaleAnimation;// 缩放动画
    private Rotate3DAnimation mThreeDAnimation;// 3D旋转动画
    private Bitmap mContentBitmap;

    private int mWindowWidth;
    private int mWindowHeight;
    private int mStatusHeight;
    private boolean isOpenBook = false;// 是否打开书籍 其实是是否离开当前界面，跳转到其他的界面

    public ImageView coverIv;
    public ImageView backgroundIv;
    public AnimationListener animationListener;

    public ReaderAnimation(Context context) {
        this.mContext = context;
        initData();
        initUI();
    }

    public void startAnimation(ItemFrameInfo frameInfo) {
        if (mContext == null
                || coverIv == null
                || backgroundIv == null
                || frameInfo == null) {
            return;
        }
        coverIv.setVisibility(View.VISIBLE);
        backgroundIv.setVisibility(View.VISIBLE);

        // 两个ImageView设置大小和位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coverIv.getLayoutParams();
        params.leftMargin = frameInfo.leftMargin;
        params.topMargin = frameInfo.topMargin - mStatusHeight;
        params.width = frameInfo.width;
        if (frameInfo.topMargin + frameInfo.height > mWindowHeight) {
            params.height = frameInfo.height * -1;// 若有出屏部分，貌似将params的高度设成负值就可以保证动画正常1
        } else {
            params.height = frameInfo.height;
        }
        coverIv.setLayoutParams(params);
        backgroundIv.setLayoutParams(params);

        //mContent = new ImageView(MainActivity.this);

        if (mContentBitmap == null) {
            mContentBitmap = Bitmap.createBitmap(frameInfo.width, frameInfo.height, Bitmap.Config.ARGB_8888);
            mContentBitmap.eraseColor(mContext.getResources().getColor(R.color.read_theme_yellow));
        }
        backgroundIv.setImageBitmap(mContentBitmap);

        initAnimation(frameInfo);

        backgroundIv.clearAnimation();
        backgroundIv.startAnimation(mScaleAnimation);
        coverIv.clearAnimation();
        coverIv.startAnimation(mThreeDAnimation);
    }

    public void reverseAnimation(ItemFrameInfo frameInfo) {
        if (mContext == null
                || coverIv == null
                || backgroundIv == null
                || mScaleAnimation == null
                || mThreeDAnimation == null
                || frameInfo == null) {
            return;
        }
        // 两个ImageView设置大小和位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coverIv.getLayoutParams();
        params.leftMargin = frameInfo.leftMargin;
        params.topMargin = frameInfo.topMargin - mStatusHeight;
        params.width = frameInfo.width;
        params.height = frameInfo.height;
        coverIv.setLayoutParams(params);
        backgroundIv.setLayoutParams(params);

        mScaleAnimation.reverse();
        mThreeDAnimation.reverse();
        backgroundIv.clearAnimation();
        backgroundIv.startAnimation(mScaleAnimation);
        coverIv.clearAnimation();
        coverIv.startAnimation(mThreeDAnimation);
    }

    public boolean isOpenBook() {
        return isOpenBook;
    }

    /****************************** Private Method  *******************************/

    private void initData() {
        if (mContext == null) {
            return;
        }
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(displayMetrics);
            mWindowWidth = displayMetrics.widthPixels;
            mWindowHeight = displayMetrics.heightPixels;

            // 获取状态栏高度
            mStatusHeight = -1;
            //获取status_bar_height资源的ID
            int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                mStatusHeight = mContext.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception ex) {
            // Do Nothing, should log error
        }
    }

    private void initUI() {
    }

    // 初始化动画
    private void initAnimation(ItemFrameInfo frameInfo) {
        if (mContext == null || frameInfo == null) {
            return;
        }
        float viewWidth = frameInfo.width;
        float viewHeight = frameInfo.height;

        float horScale = mWindowWidth / viewWidth;
        float verScale = mWindowHeight / viewHeight;

        mScaleAnimation = new ContentScaleAnimation(frameInfo.leftMargin, frameInfo.topMargin - mStatusHeight, horScale, verScale, false);
        mScaleAnimation.setInterpolator(new DecelerateInterpolator());  //设置插值器
        mScaleAnimation.setDuration(1000);
        mScaleAnimation.setFillAfter(true);  //动画停留在最后一帧
        mScaleAnimation.setAnimationListener(mAnimationListener);

        mThreeDAnimation = new Rotate3DAnimation(mContext, -180, 0, frameInfo.leftMargin, frameInfo.topMargin - mStatusHeight, horScale, verScale, true);
        mThreeDAnimation.setDuration(1000);                         //设置动画时长
        mThreeDAnimation.setFillAfter(true);                        //保持旋转后效果
        mThreeDAnimation.setInterpolator(new DecelerateInterpolator());
    }


    /****************************** Listener Impl Method  *******************************/

    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mScaleAnimation.hasEnded() && mThreeDAnimation.hasEnded()) {
                // 两个动画都结束的时候再处理后续操作

                if (!isOpenBook) {
                    isOpenBook = true;
                    if (animationListener != null) {
                        animationListener.openReader();
                    }
                } else {
                    isOpenBook = false;
                    coverIv.clearAnimation();
                    backgroundIv.clearAnimation();
                    coverIv.setVisibility(View.GONE);
                    backgroundIv.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /****************************** Class & Interface Declaration  *******************************/

    public static class ItemFrameInfo {
        public int width;
        public int height;
        public int topMargin;
        public int leftMargin;
    }

    public interface AnimationListener {
        void openReader();
    }
}
