package com.zxinglib.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.zxing.Result;
import com.zxinglib.camera.CameraManager;

/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 *
 */
public abstract class QrCapture implements SurfaceHolder.Callback {
    private Activity activity;
    // 相机控制
    private CameraManager cameraManager;
    private CaptureHandler handler;
    private boolean hasSurface;
    // 电量控制
    private InactivityTimer inactivityTimer;
    // 声音、震动控制
    private BeepManager beepManager;
    private boolean isInitialized;

    /**
     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）
     */
    public QrCapture(Activity activity) {
        this.activity = activity;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(activity);
        beepManager = new BeepManager(activity);
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public Activity getActivity() {
        return activity;
    }

    public void onResume() {
        if (!isInitialized) {
            SurfaceView surfaceView = getSurfaceView();
            if (surfaceView != null) {
                // CameraManager必须在这里初始化，而不是在onCreate()中。
                // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
                // 当扫描框的尺寸不正确时会出现bug
                cameraManager = new CameraManager(activity.getApplication());
                setScanArea(cameraManager);
                handler = null;
                SurfaceHolder surfaceHolder = surfaceView.getHolder();
                if (hasSurface) {
                    // activity在paused时但不会stopped,因此surface仍旧存在；
                    // surfaceCreated()不会调用，因此在这里初始化camera
                    initCamera(surfaceHolder);
                } else {
                    // 重置callback，等待surfaceCreated()来初始化camera
                    surfaceHolder.addCallback(this);
                }

                beepManager.updateConfig();
                inactivityTimer.onResume();
                isInitialized = true;
            }
        }
    }

    protected abstract SurfaceView getSurfaceView();
    protected abstract void setScanArea(CameraManager cameraManager);

    public boolean isInitialized() {
        return isInitialized;
    }

    public void onPause() {
        isInitialized = false;
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = getSurfaceView();
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    public void onDestroy() {
        inactivityTimer.shutdown();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * 扫描成功，处理反馈信息
     */
    public void handleDecode(Result result, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
        if (barcode != null) {
            beepManager.playBeepSoundAndVibrate();
        }
        handleResult(result, barcode, scaleFactor);
    }

    protected abstract void handleResult(Result result, Bitmap barcode, float scaleFactor);

    /**
     * 初始化Camera
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            onCamerainitFaile("SurfaceHolder is null");
            return;
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureHandler(this, null, null, null, cameraManager);
            }
        } catch (Exception e) {
            onCamerainitFaile(e.getMessage());
        }
    }

    protected abstract void onCamerainitFaile(String msg);

    public void restartScan() {
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }
}
