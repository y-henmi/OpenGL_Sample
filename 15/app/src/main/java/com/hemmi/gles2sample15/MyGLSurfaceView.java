package com.hemmi.gles2sample15;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by tommy on 2015/06/18.
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private final Context mContext;
    private GLRenderer renderer;
    private View UpperLayout;
    private static final String TAG = "MyGLSurfaceView";
    private GestureDetector mGestureDetector;

    // サーフェースビューのコンストラクタ
    //public MyGLSurfaceView(Context context) {
    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        //super(context);
        super(context, attrs);
        mContext=context;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        renderer = new GLRenderer(context);
        setRenderer(renderer);
        mGestureDetector = new GestureDetector(mOnGestureListener);
    }

    public void setUpperLayout(View ul){
        UpperLayout=ul;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }


    private GestureListener mOnGestureListener
            = new GestureListener() {

        @Override
        public void onFirstTouch(GestureDetector detector) {
            Log.i(TAG, "onFirstTouch:" + detector.getX1() + ", " + detector.getY1());
        }

        @Override
        public void onSecondTouch(GestureDetector detector) {
            Log.i(TAG, "onSecondTouch:" + detector.getX2() + ", " + detector.getY2());
        }

        @Override
        public void onSecondTouchEnd(GestureDetector detector) {
            Log.i(TAG, "onSecondTouchEnd:");
        }

        @Override
        public void onFirstTouchEnd(GestureDetector detector) {
            Log.i(TAG, "onFirstTouchEnd:");
        }

        @Override
        public void onShortTap(GestureDetector detector) {
            Toast.makeText(mContext, "detected short tap", Toast.LENGTH_SHORT).show();
            float DeltaX = detector.getAccumulatedDeltaX();
            float DeltaY = detector.getAccumulatedDeltaY();
            renderer.setScrollValue(-DeltaX, -DeltaY);//shorttapなのに動いてしまったのを修正
            UpperLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLongTap(GestureDetector detector) {
            Toast.makeText(mContext, "detected long tap", Toast.LENGTH_SHORT).show();
            float DeltaX = detector.getAccumulatedDeltaX();
            float DeltaY = detector.getAccumulatedDeltaY();
            renderer.setScrollValue(-DeltaX, -DeltaY);//longtapなのに動いてしまったのを修正
        }

        @Override
        public void onOneFingerMove(GestureDetector detector) {
            float DeltaX = detector.getDeltaX();
            float DeltaY = detector.getDeltaY();
            Log.i(TAG, "onOneFingerMove: DeltaX, DeltaY = " + DeltaX + "," + DeltaY);
            renderer.setScrollValue(DeltaX, DeltaY);
        }

        @Override
        public void onTwoFingerMove(GestureDetector detector) {
            float factor = detector.getFactor();
            float Delta2X = detector.getDelta2X();
            float Delta2Y = detector.getDelta2Y();
            float Rotation = detector.getRotation();
            Log.i(TAG, "onTwoFingerMove: factor = " + factor);
            Log.i(TAG, "onTwoFingerMove: Delta2X, Delta2Y = " + Delta2X + "," + Delta2Y);
            Log.i(TAG, "onTwoFingerMove: Rotation = " + Rotation);
            renderer.setPinch2(factor);
            renderer.setRot2Angle(Rotation);
            renderer.setScroll2Value(Delta2X, Delta2Y);
        }

        @Override
        public void onThreeFingerMove(GestureDetector detector) {
            float factor = detector.getFactor();
            float Delta2X = detector.getDelta2X();
            float Delta2Y = detector.getDelta2Y();
            float Rotation = detector.getRotation();
            Log.i(TAG, "onThreeFingerMove: factor = " + factor);
            Log.i(TAG, "onThreeFingerMove: Delta2X, Delta2Y = " + Delta2X + "," + Delta2Y);
            Log.i(TAG, "onThreeFingerMove: Rotation = " + Rotation);
            renderer.setPinch3(factor);
            renderer.setRot3Angle(Rotation);
            renderer.setScroll3Value(Delta2X, Delta2Y);
        }
    };

    //メニューの受付
    public void setRotationVelocity(float rotationvelocity) {
        renderer.setRotationVelocity(rotationvelocity);
    }
}
