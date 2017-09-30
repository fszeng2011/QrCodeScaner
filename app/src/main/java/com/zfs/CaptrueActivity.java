package com.zfs;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zxinglib.android.QrCapture;
import com.zxinglib.camera.CameraManager;

public class CaptrueActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    ImageView captureScanLine;
    Captrue captrue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captrue);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        captureScanLine = (ImageView) findViewById(R.id.capture_scan_line);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -0.23f, Animation.RELATIVE_TO_PARENT,
                0.77f);
        animation.setDuration(2000);
        animation.setRepeatCount(-1);
        captureScanLine.startAnimation(animation);
        captrue = new Captrue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        captrue.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captrue.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captrue.onDestroy();
    }

    public void restartScan(View view) {
        captrue.restartScan();
    }

    private class Captrue extends QrCapture {
        /**
         * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）
         */
        public Captrue(Activity activity) {
            super(activity);
        }

        @Override
        protected SurfaceView getSurfaceView() {
            return surfaceView;
        }

        @Override
        protected void setScanArea(CameraManager cameraManager) {
            cameraManager.setManualFramingRect(dip2px(240), dip2px(240), -1, (int) (dip2px(50) + getStatusBarHeight() +
						getActionBarSize(CaptrueActivity.this)));
        }

        @Override
        protected void handleResult(Result result, Bitmap barcode, float scaleFactor) {
            Toast.makeText(CaptrueActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCamerainitFaile() {
            Toast.makeText(CaptrueActivity.this, "摄像头打开失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

	/**
	 * 获取ActionBar的高度
	 */
	public static float getActionBarSize(Context context) {
		TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
		float height = ta.getDimension(0, 0);
		ta.recycle();
		return height;
	}
    
    /**
     * 获取状态栏高度
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素) 
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
