package com.hemmi.gles2sample15;

import android.view.MotionEvent;

/**
 * Created by tommy on 2015/09/08.
 */
public class GestureDetector {
    private GestureListener mListener;
    private int[] mPointerID = {
            -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1
    };
    private int nValidPointers = 0;
    private final int nPointers = 20;
    private float mOrgX=0f, mOrgY=0f;//初期値
    private float[] mCurnX = new float[3]; //今回のX
    private float[] mCurnY = new float[3]; //今回のX
    private float[] mPrevX = new float[3]; //前回のX
    private float[] mPrevY = new float[3]; //前回のX
    private float mDeltaX, mDeltaY;//１本指の時の移動量
    private float mDelta2X, mDelta2Y, mRotation;//2本指または3本指の時の移動量
    private float mFactor;//2本指または3本指の時の拡大率
    private long mBeginTime=0,mEndTime=1;

    private final long TimeConstant=400;  //シングルショートタップ時間　[msec]
    private final long TimeConstant1A=50;  //切り替え不感時間　[msec]
    private final long TimeConstant1B=100;  //切り替え不感時間　[msec]
    private final long TimeConstant2=2000;  //最短シングルロングタップ時間　[msec]
    private final long TimeConstant3=4000;  //最長シングルロングタップ時間　[msec]
    private final float DisplacementLimit=200f;  //ロングタップ時の移動許容定義(距離の２乗)

    public GestureDetector(GestureListener listener)
    {
        mListener = listener;
    }

    // タッチ処理　２本指まで対応する
    // mPointerID1が常に優先
    public boolean onTouchEvent(MotionEvent event) {
        int ActionMasked = event.getActionMasked();
        int ActionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(ActionIndex);
        float currentX = event.getX(ActionIndex);
        float currentY = event.getY(ActionIndex);
        long EventTime = event.getEventTime();
        float angle1;
        float angle2;
        float length1;
        float length2;
        int i,range;

        switch (ActionMasked) {
            case MotionEvent.ACTION_DOWN://最初のタッチ
                mPointerID[0] = pointerId;
                mOrgX = mCurnX[0] = mPrevX[0] = currentX;
                mOrgY = mCurnY[0] = mPrevY[0] = currentY;
                nValidPointers = 1;
                mBeginTime=EventTime;
                mListener.onFirstTouch(this);
                break;

            case MotionEvent.ACTION_POINTER_DOWN://2つ目以降のタッチ
                if (nValidPointers < nPointers) {
                    mPointerID[nValidPointers] = pointerId;
                    if (nValidPointers < 3) { //これまでに2個以下しか見つかっていない　1か2
                        mOrgX = mCurnX[nValidPointers] = mPrevX[nValidPointers] = currentX;
                        mOrgY = mCurnY[nValidPointers] = mPrevY[nValidPointers] = currentY;
                        for (i = 0; i < nValidPointers; i++) {
                            int ptrIndex = event.findPointerIndex(mPointerID[i]);
                            mCurnX[i] = mPrevX[i] = event.getX(ptrIndex);
                            mCurnY[i] = mPrevY[i] = event.getY(ptrIndex);
                        }
                        mBeginTime=EventTime;
                        mListener.onSecondTouch(this);
                    }
                    nValidPointers++;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP://２つ目以降のタッチの終了
                int found = -1;
                for (i=0; i<nValidPointers; i++) {
                    if (mPointerID[i] == pointerId) {
                        found=i;
                        break;
                    }
                }
                if (found!=-1) {
                    for (; i < nValidPointers - 1; i++) mPointerID[i] = mPointerID[i + 1];
                    mPointerID[i] = -1;
                    nValidPointers--;
                    range = 3;
                    if (nValidPointers < range) range = nValidPointers;
                    for (i = 0; i < range; i++) {
                        int ptrIndex = event.findPointerIndex(mPointerID[i]);
                        mCurnX[i] = mPrevX[i] = event.getX(ptrIndex);
                        mCurnY[i] = mPrevY[i] = event.getY(ptrIndex);
                    }
                    mEndTime = mBeginTime = EventTime;
                    mListener.onSecondTouchEnd(this);
                }
                break;

            case MotionEvent.ACTION_UP://タッチすべて終了
            case MotionEvent.ACTION_CANCEL://キャンセル
                if (nValidPointers==1) {
                    if (mEndTime!=mBeginTime) {
                        long deltat= EventTime - mBeginTime;
                        float dx = mOrgX - currentX;
                        float dy = mOrgY - currentY;
                        float d2=dx*dx+dy*dy;
                        if (deltat<TimeConstant && d2<DisplacementLimit) {
                            mListener.onShortTap(this);
                        } else if (TimeConstant2<deltat && deltat<TimeConstant3 && d2<DisplacementLimit) {
                            mListener.onLongTap(this);
                        }
                    }
                    mListener.onFirstTouchEnd(this);
                }
                for (i=0; i<nPointers; i++) mPointerID[i] = -1;
                nValidPointers=0;
                break;

            case MotionEvent.ACTION_MOVE://指が移動中
                if (EventTime-mEndTime<TimeConstant1B || EventTime-mBeginTime<TimeConstant1A) break;
                range=3;
                if (nValidPointers<range) range = nValidPointers;
                for (i = 0; i < range; i++) {
                    int ptrIndex = event.findPointerIndex(mPointerID[i]);
                    mCurnX[i] = event.getX(ptrIndex);
                    mCurnY[i] = event.getY(ptrIndex);
                }
                if (1<nValidPointers && nValidPointers<4) { //2本指または3本指の時
                    angle1 = calcAngle(mPrevX[0], mPrevY[0], mPrevX[1], mPrevY[1]);
                    angle2 = calcAngle(mCurnX[0], mCurnY[0], mCurnX[1], mCurnY[1]);
                    length1 = calcLength(mPrevX[0], mPrevY[0], mPrevX[1], mPrevY[1]);
                    length2 = calcLength(mCurnX[0], mCurnY[0], mCurnX[1], mCurnY[1]);
                    if (nValidPointers==3) { //3本指の時
                        length1 += calcLength(mPrevX[1], mPrevY[1], mPrevX[2], mPrevY[2]);
                        length2 += calcLength(mCurnX[1], mCurnY[1], mCurnX[2], mCurnY[2]);
                        length1 += calcLength(mPrevX[2], mPrevY[2], mPrevX[0], mPrevY[0]);
                        length2 += calcLength(mCurnX[2], mCurnY[2], mCurnX[0], mCurnY[0]);
                    }
                    mRotation = angle2 - angle1;
                    if (mRotation < -180.0f) {
                        mRotation += 360.0f;
                    } else if (mRotation > 180.0f) {
                        mRotation -= 360.0f;
                    }

                    mDelta2X = calcCenter(mCurnX[0], mCurnX[1]) - calcCenter(mPrevX[0], mPrevX[1]);
                    mDelta2Y = calcCenter(mCurnY[0], mCurnY[1]) - calcCenter(mPrevY[0], mPrevY[1]);

                    if (0<length1) {
                        mFactor = length2 / length1;
                    } else {
                        mFactor = 1f;
                    }
                } else { //nValidPointers==1 1本指の時
                    mDeltaX = mCurnX[0] - mPrevX[0];
                    mDeltaY = mCurnY[0] - mPrevY[0];
                }
                for (i = 0; i < range; i++) {
                    mPrevX[i] = mCurnX[i];
                    mPrevY[i] = mCurnY[i];
                }
                if (nValidPointers==3){
                    mListener.onThreeFingerMove(this);
                } else if (nValidPointers==2) {
                    mListener.onTwoFingerMove(this);
                } else if (nValidPointers==1) {
                    mListener.onOneFingerMove(this);
                }
                break;
        }
        return true;
    }

    // 中点を計算する。
    private float calcCenter(float p1, float p2)
    {
        return (p1 + p2) / 2;
    }

    // 線分から角度[deg]を計算する。
    private float calcAngle(float x1, float y1, float x2, float y2)
    {
        return (float) Math.toDegrees(Math.atan2((y2 - y1), (x2 - x1)));
    }

    // 線分長を計算する。
    private float calcLength(float x1, float y1, float x2, float y2)
    {
        float dx = x2-x1;
        float dy = y2-y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float getX1()
    {
        return mCurnX[0];
    }

    public float getY1()
    {
        return mCurnY[0];
    }

    public float getX2()
    {
        return mCurnX[1];
    }

    public float getY2()
    {
        return mCurnY[1];
    }

    //１本指スワイプ時のｘ方向移動量
    public float getDeltaX()
    {
        return mDeltaX;
    }

    //１本指スワイプ時のｙ方向移動量
    public float getDeltaY()
    {
        return mDeltaY;
    }

    //１本指スワイプ時の累積ｘ方向移動量
    public float getAccumulatedDeltaX()
    {
        return mCurnX[0]-mOrgX;
    }

    //１本指スワイプ時の累積ｙ方向移動量
    public float getAccumulatedDeltaY()
    {
        return mCurnY[0]-mOrgY;
    }

    //２本指３本指スワイプ時のｘ方向移動量
    public float getDelta2X()
    {
        return mDelta2X;
    }

    //２本指３本指スワイプ時のｙ方向移動量
    public float getDelta2Y()
    {
        return mDelta2Y;
    }

    //２本指３本指スワイプ時の回転量[deg]
    public float getRotation()
    {
        return mRotation;
    }

    //２本指３本指スワイプ時の拡大率
    public float getFactor()
    {
        return mFactor;
    }

}
