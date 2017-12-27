package com.hemmi.showobjfilesample;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class GLRenderer implements GLSurfaceView.Renderer{
    private TexReg mTex;//自作のTexture設定クラス→⑤のクラス
    private ObjDrawer mObj,mCube;//Objファイルの表示クラス→④のクラス
    private float angle = 0f;
    private float lightpos[] = {0f,0f,1f,1f};//ライティングの位置

    private final int[] resname = {R.raw.newtexture,R.raw.cubetex};//使用するTextureの画像ファイルリソースの指定。
    //コンストラクタ
    public GLRenderer(Context _context){
        mTex = new TexReg(_context,resname.length);
        mObj = new ObjDrawer(_context.getResources(),R.raw.shogi_koma);//使用するOBJファイルのリソース指定。
        mCube = new ObjDrawer(_context.getResources(),R.raw.cubetest2);//同じくOBJファイル指定
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightpos,0);
        GLU.gluLookAt(gl, 0f, 0f, -1.0f, 0f, 0f, 0f, 0f, 1.0f, 0f);

        gl.glPushMatrix();
        mTex.TexUse(gl,0);

        gl.glTranslatef(0f, 1.8f, 10.0f);
        angle += 1.0f;
        gl.glRotatef(angle, 0f, 1f, 1f);

        mObj.draw(gl);
        gl.glPopMatrix();

        mTex.TexUse(gl, 1);
        gl.glTranslatef(0f, -1.8f, 10.0f);
        gl.glRotatef(-angle, 1f, 0f, 1f);
        mCube.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int _width, int _hight) {
        float ratio = (float) _width/_hight;

        gl.glViewport(0, 0, _width, _hight);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluPerspective(gl, 30.0f, ratio, 1.0f, 40.0f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_FASTEST);

        gl.glClearColor(0,0,0,1);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);

        mTex.TexInit(gl,resname);
    }

}

