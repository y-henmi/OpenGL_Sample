package com.hemmi.gles2sample15;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by tommy on 2016/10/13.
 * SphereEXをテクスチャが貼れるように拡張したクラスです
 */

public class TexSphereEX extends SphereEX {
    //bufferの定義
    private FloatBuffer texcoordBuffer;

    TexSphereEX(){}
    TexSphereEX(float maxlongitude, float minlatitude, float maxlatitude, int nSlices, int nStacks) {
        makeSphere(maxlongitude, minlatitude, maxlatitude, nSlices, nStacks);
        makeTexSphere(maxlongitude, minlatitude, maxlatitude, nSlices, nStacks);
    }
    private void makeTexSphere(float maxlongitude, float minlatitude, float maxlatitude, int nSlices, int nStacks) {
        int sizeArray=nPoints*2;
        float textcoords[] = new float[sizeArray];
        float dx = 1f/(float)nSlices;
        float dy = 1f/(float)nStacks;
        int p = 0;
        for (int i=0; i <= nSlices; i++) {
            for (int j = 0; j <= nStacks; j++) {
                textcoords[p++] = i * dx;        // x
                textcoords[p++] = 1f - j * dy;   // y
            }
        }
        /************************************
         nStacks = 4, nSlices = 5 のときの点の位置
         4 9 14 19 24 29
         3 8 13 18 23 28
         2 7 12 17 22 27
         1 6 11 16 21 26
         0 5 10 15 20 25
         texture座標系
         (0,0)               (1,0) →x

         (0,1)               (1,1)
         ↓y
         *************************************/
        texcoordBuffer = BufferUtil.makeFloatBuffer(textcoords);
    }

    public void draw(float r,float g,float b,float a, float shininess){
        //頂点点列のテクスチャ座標
        GLES20.glVertexAttribPointer(GLES.texcoordHandle, 2,
                GLES20.GL_FLOAT, false, 0, texcoordBuffer);

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

        }

        //描画
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                nIndexs, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

    }
}
