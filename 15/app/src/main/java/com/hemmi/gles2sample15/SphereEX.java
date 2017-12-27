package com.hemmi.gles2sample15;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by tommy on 2016/10/11.
 * 球を描画する。半球描画もできる。
 * 一般的には球の一部を描画し、特別な場合として球全体も描画するクラスである。
 * 球の中心は原点にあり、半径は１である。
 * 球の赤道はzx平面上にある。
 * 球の両極（地球では北極と南極）はｙ軸上にあり、北極がy軸+、南極がy軸-とする。
 * 緯度は赤道が0、北極は+90、南極は-90となる。
 * 経度はz軸＋方向が0で、x軸＋方向が90となり、z軸＋方向は再び360となる。
 * 球の一部は緯度最小値minlatitude[deg]から緯度最大値maxlatitude[deg]まで、
 * 経度0から経度最大値maxlongitude[deg]までの３次元扇形となる。
 *  minlatitude < maxlatitude でなければならない。
 * nSlicesは赤道方向にパーツをいくつに分けるかを与え，
 * nStacksは赤道と垂直方向にパーツをいくつに分けるかを与える。
 * ３次元扇形は（nSlices×nStacks）個の微小３次元扇形に分割される。
 *
 * インスタンス生成例
 * SphereEX mSphereEX = new SphereEX(360f,-90f,90f,72,36); //球を定義
 * SphereEX mSphereEX = new SphereEX(360f,0f,90f,72,36); //zx平面より+y側にある半球を定義
 * SphereEX mSphereEX = new SphereEX(180f,-90f,90f,72,36); //yz平面より+x側にある半球を定義
 */

public class SphereEX {
    SphereEX(){}
    SphereEX(float maxlongitude, float minlatitude, float maxlatitude, int nSlices, int nStacks) {
        makeSphere(maxlongitude, minlatitude, maxlatitude, nSlices, nStacks);
    }

    //bufferの定義
    protected FloatBuffer vertexBuffer;
    protected ShortBuffer indexBuffer;
    protected FloatBuffer normalBuffer;

    protected int nIndexs;
    protected int nPoints;

    protected void makeSphere(float maxlongitude, float minlatitude, float maxlatitude, int nSlices, int nStacks) {
        int nSlices1=nSlices+1;
        int nStacks1=nStacks+1;
        nPoints = nSlices1*nStacks1;
        int sizeArray=nPoints*3;
        //頂点3Dベクトル
        float[] vertexs= new float[sizeArray];
        double dlongitude=3.141592653589793/180.*maxlongitude/nSlices;
        double dlatitude=3.141592653589793/180.*(maxlatitude-minlatitude)/nStacks;
        double longitude, latitude;
        double minlatitudeR=3.141592653589793/180.*minlatitude;
        int p=0;
        for (int i=0; i<nSlices1; i++) {
            longitude = i*dlongitude;
            for (int j=0; j<nStacks1; j++) {
                latitude = minlatitudeR + j * dlatitude;
                vertexs[p++]=(float)(Math.cos(latitude)* Math.sin(longitude));      //x
                vertexs[p++]=(float) Math.sin(latitude);                            //y
                vertexs[p++]=(float)(Math.cos(latitude)* Math.cos(longitude));      //z
            }
        }
        //頂点の3D法線ベクトル 頂点座標と同じになる
        //float[] normals= new float[sizeArray];
        //for (int i=0;i<sizeArray;i++) normals[i]=vertexs[i];

        //頂点座標番号列
        nIndexs=nStacks1*2*nSlices;
        short [] indexs= new short[nIndexs];
        p=0;
        for (int i=0;i<nSlices;i++) {
            for (int j=0;j<nStacks1;j++) {
                indexs[p++]=(short)(i*nStacks1+j);
                indexs[p++]=(short)((i+1)*nStacks1+j);
            }
        }
        /************************************
        nStacks = 4, nSlices = 5 のとき
        4 9 14 19 24 29
        3 8 13 18 23 28
        2 7 12 17 22 27
        1 6 11 16 21 26
        0 5 10 15 20 25
        *************************************/

        vertexBuffer=BufferUtil.makeFloatBuffer(vertexs);
        indexBuffer=BufferUtil.makeShortBuffer(indexs);
        normalBuffer=vertexBuffer; //同じでよいので，同じ実体を用いる
    }

    public void draw(float r,float g,float b,float a, float shininess){
        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        if (GLES.checkLiting()) {
            //頂点での法線ベクトル
            GLES20.glVertexAttribPointer(GLES.normalHandle, 3,
                    GLES20.GL_FLOAT, false, 0, normalBuffer);

            //周辺光反射
            GLES20.glUniform4f(GLES.materialAmbientHandle, r, g, b, a);

            //拡散反射
            GLES20.glUniform4f(GLES.materialDiffuseHandle, r, g, b, a);

            //鏡面反射
            GLES20.glUniform4f(GLES.materialSpecularHandle, 1f, 1f, 1f, a);
            GLES20.glUniform1f(GLES.materialShininessHandle, shininess);

        } else {
            //shadingを使わない時に使う単色の設定 (r, g, b,a)
            GLES20.glUniform4f(GLES.objectColorHandle, r, g, b, a);
        }

        //
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                nIndexs, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }
    /*
    public void drawline(float r,float g,float b,float a){
        //頂点点列
        GLES20.glVertexAttribPointer(GLES.positionHandle, 3,
                GLES20.GL_FLOAT, false, 0, vertexBuffer);

        //shadingを使わない時に使う単色の設定 (r, g, b,a)
        GLES20.glUniform4f(GLES.objectColorHandle, 1f, 1f, 1f, a);

        //
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                nIndexs, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }
    */
}
