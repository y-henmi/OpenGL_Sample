package com.hemmi.showobjfilesample;

import android.content.res.Resources;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ObjDrawer {
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexcoordBuffer;
    private FloatBuffer mNormalsBuffer;
    private ShortBuffer mIndexBuffer;
    private int indcout;
    //コンストラクタ
    public ObjDrawer(Resources res,int fileres){
//final ObjLoaderTex obj = new ObjLoaderTex(res,fileres);
        ObjFileLoader obj = new ObjFileLoader();
        obj.FileLoad(res.openRawResource(fileres), 2.0f);

        float vertices[] = obj.getVertex();
        float texcoords[] = obj.getTexcoord();
        float normals[] = obj.getNormals();
        short indices[] = obj.getIndex();

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(texcoords.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTexcoordBuffer = tbb.asFloatBuffer();
        mTexcoordBuffer.put(texcoords);
        mTexcoordBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalsBuffer = nbb.asFloatBuffer();
        mNormalsBuffer.put(normals);
        mNormalsBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length*2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

        indcout = obj.getIndexCount();
    }

    public void draw(GL10 gl)
    {
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalsBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexcoordBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indcout, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
}