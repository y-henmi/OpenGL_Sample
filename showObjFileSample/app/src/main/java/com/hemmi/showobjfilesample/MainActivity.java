package com.hemmi.showobjfilesample;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

public class OpenGLTest extends Activity {
    private GLSurfaceView mGLSurfaceView;
    private FrameLayout mFrame;
    private GLRenderer mRend;//自作Rendererクラス→②のクラスです。

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//アプリタイトルの消去
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

//OpenGL下準備
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRend = new GLRenderer(this);
        mGLSurfaceView.setRenderer(mRend);
        mFrame = (FrameLayout) findViewById(R.id.frame);
        mFrame.addView(mGLSurfaceView);
    }

    @Override
    public void onPause(){
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mGLSurfaceView.onResume();
    }
}