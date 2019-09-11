package com.orient.test.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.orient.test.R;
import com.orient.test.adapter.BookAdapter;
import com.orient.test.animation.ReaderAnimation;

import java.util.ArrayList;
import java.util.List;

public class OpenBookActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {
    private static final String TAG = "OpenBookActivity";

    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    // 资源文件列表
    private List<Integer> values = new ArrayList<>();
    // 记录View的位置
    private int[] location = new int[2];
    // 第一个View的位置
    private int[] firstItemLocation = new int[2];
    private int itemWidth;
    private int itemHeight;

    private ReaderAnimation mReaderAnimation;

    public static void show(Context context) {
        Intent intent = new Intent(context, OpenBookActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        initWidget();
    }

    private void initWidget() {
        mRecyclerView = findViewById(R.id.recycle);
        mReaderAnimation = new ReaderAnimation(this);
        mReaderAnimation.coverIv = findViewById(R.id.img_first);
        mReaderAnimation.backgroundIv = findViewById(R.id.img_content);
        mReaderAnimation.animationListener = mAnimationListener;

        initData();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new BookAdapter(values, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    // 重复添加数据
    private void initData() {
        for (int i = 0; i < 10; i++) {
            values.add(R.drawable.preview);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 当界面重新进入的时候进行合书的动画
        if (mReaderAnimation != null && mReaderAnimation.isOpenBook()) {
            // 两个ImageView设置大小和位置
            ReaderAnimation.ItemFrameInfo frameInfo = new ReaderAnimation.ItemFrameInfo();
            frameInfo.leftMargin = firstItemLocation[0];
            frameInfo.topMargin = firstItemLocation[1];
            frameInfo.width = itemWidth;
            frameInfo.height = itemHeight;
            mReaderAnimation.reverseAnimation(frameInfo);
        }
    }


    private ReaderAnimation.AnimationListener mAnimationListener = new ReaderAnimation.AnimationListener() {
        @Override
        public void openReader() {
            BookSampleActivity.show(OpenBookActivity.this);
        }
    };

    @Override
    public void onItemClick(int pos, View view) {
        if (pos == 0) {
            view.getLocationInWindow(firstItemLocation);
        }

        // 计算当前的位置坐标
        view.getLocationInWindow(location);
        itemWidth = view.getWidth();
        itemHeight = view.getHeight();
        ReaderAnimation.ItemFrameInfo itemFrameInfo = new ReaderAnimation.ItemFrameInfo();
        itemFrameInfo.leftMargin = location[0];
        itemFrameInfo.topMargin = location[1];
        itemFrameInfo.width = itemWidth;
        itemFrameInfo.height = itemHeight;

        Bitmap coverBitmap = BitmapFactory.decodeResource(getResources(), values.get(pos));
        mReaderAnimation.coverIv.setImageBitmap(coverBitmap);

        mReaderAnimation.startAnimation(itemFrameInfo);
    }
}
