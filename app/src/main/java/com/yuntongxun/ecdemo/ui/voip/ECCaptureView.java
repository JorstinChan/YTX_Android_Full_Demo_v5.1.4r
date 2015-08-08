package com.yuntongxun.ecdemo.ui.voip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * com.yuntongxun.ecdemo.ui.voip in ECDemo_Android
 * Created by Jorstin on 2015/7/6.
 */
public class ECCaptureView extends SurfaceView
        implements SurfaceHolder.Callback {

    public SurfaceHolder mSurfaceHolder;


    public ECCaptureView(Context context) {
        super(context);
        initCaptureView();
    }

    public ECCaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCaptureView();
    }

    public ECCaptureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initCaptureView();
    }

    private void initCaptureView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        try {
            mSurfaceHolder.removeCallback(this);
            mSurfaceHolder = holder;
            mSurfaceHolder.addCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
