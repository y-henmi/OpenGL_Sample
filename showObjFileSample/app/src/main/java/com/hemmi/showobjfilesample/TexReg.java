package com.hemmi.showobjfilesample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class TexReg {
    private Context context;
    private int[] texs;
    private int[] Tex_ID;
    private Bitmap bmp;
    private int tc;

    //コンストラクタ
    public TexReg(Context _context,int cnt){
        tc = cnt;
        texs = new int[tc];
        Tex_ID = new int[tc];

        context = _context;
    }

    public void TexInit(GL10 gl,int resname[]){
        InputStream in;
        int[] units = new int[1];
        int[] maxSize = new int[1];

        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_UNITS, units,0);
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSize,0);
        if(tc > units[0]) tc = units[0];

        for(int i=0;i<tc;i++){
            in = context.getResources().openRawResource(resname[i]);
            try{
                bmp = BitmapFactory.decodeStream(in);
            }finally{
                try{
                    in.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            gl.glGenTextures(tc, texs, 0);
            Tex_ID[i] = texs[i];
            gl.glEnable(GL10.GL_TEXTURE_2D);

            gl.glBindTexture(GL10.GL_TEXTURE_2D, Tex_ID[i]);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_NEAREST);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
            bmp.recycle();
        }
    }

    public void TexUse(GL10 gl,int num){
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);

        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, Tex_ID[num]);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);
    }
}

