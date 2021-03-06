package com.iqiyi.yangdaokuan.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class ParticlesActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        glSurfaceView = new GLSurfaceView(this);
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.startsWith("google_sdk")
                || Build.FINGERPRINT.startsWith("Enulator")
                || Build.FINGERPRINT.startsWith("Android SDK built for x86")));

        final ParticlesRenderer particlesRenderer = new ParticlesRenderer(this);
        if (supportEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(particlesRenderer);
            renderSet = true;
        } else {
            return;
        }
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {

            private float previousX, previousY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent != null) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = motionEvent.getX();
                        previousY = motionEvent.getY();
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltax = motionEvent.getX() - previousX;
                        final float deltay = motionEvent.getY() - previousY;
                        previousX = motionEvent.getX();
                        previousY = motionEvent.getY();
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                particlesRenderer.handleTouchDrag(deltax, deltay);
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (renderSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (renderSet) {
            glSurfaceView.onResume();
        }
    }
}
