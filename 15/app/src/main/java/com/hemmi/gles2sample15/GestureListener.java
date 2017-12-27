package com.hemmi.gles2sample15;

/**
 * Created by tommy on 2015/09/08.
 */
public interface GestureListener {

    public void onFirstTouch(GestureDetector detector);

    public void onSecondTouch(GestureDetector detector);


    public void onSecondTouchEnd(GestureDetector detector);


    public void onFirstTouchEnd(GestureDetector detector);


    public void onShortTap(GestureDetector detector);


    public void onLongTap(GestureDetector detector);


    public void onOneFingerMove(GestureDetector detector);


    public void onTwoFingerMove(GestureDetector detector);


    public void onThreeFingerMove(GestureDetector detector);

}
