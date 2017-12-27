package com.hemmi.gles2sample15;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by tommy on 2015/06/18.
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    //システム
    private final Context mContext;
    private boolean validProgram=false; //シェーダプログラムが有効
    private float aspect;//アスペクト比

    private float viewlength0 = 5.0f; //視点距離
    private float viewlength = 5.0f; //視点距離
    private float viewingangle0 = 45f; //視野角（画面長手方向に対応する視野角[deg]）
    private float viewingangle = 45f; //視野角（画面長手方向に対応する視野角[deg]）
    private float RotAngle=0f;       //2本指回転量
    private float AngleHor=0f;       //横移動角（2本指）[deg]
    private float AngleVer=0f;       //縦移動角（2本指）[deg]

    private float   angle=0f; //回転角度
    private float rotatatinvelocity=1.f;

    //視点変更テスト変数
    private float alph=0f,beta=0f;

    //光源の座標　x,y,z
    private  float[] LightPos={50f,0f,0f,1f};//x,y,z,1
    //光源の環境光
    private float[] LightAmb={.01f,.01f,.01f,1f}; //r,g,b,a
    //光源の乱反射光
    private float[] LightDif={1f,1f,1f,1f}; //r,g,b,a
    //光源の鏡面反射反射光
    private float[] LightSpc={.01f,.01f,.01f,1f}; //r,g,b,a

    //変換マトリックス
    private  float[] pMatrix=new float[16]; //プロジェクション変換マトリックス
    private  float[] mMatrix=new float[16]; //モデル変換マトリックス
    private  float[] cMatrix=new float[16]; //カメラビュー変換マトリックス

    //モデル座標系の原点
    private  float[] origin= {0f,0f,0f,1f};

    private Axes MyAxes= new Axes();  //原点周囲の軸表示とためのオブジェクトを作成

    private SphereEX m1SphereEX = new SphereEX(360f,-90f,90f,72,36); //半径１の球
    private TexSphereEX m1TexSphereEX =new TexSphereEX(360f,-90f,90f,72,36); //半径１の球


    private Texture EarthPicture;
    private Texture MoonPicture;

    private float SunRadius=2f;
    private float EarthRadius=0.2f;
    private float MoonRadius=0.1f;

    GLRenderer(final Context context) {
        mContext = context;
    }

    //サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //プログラムの生成
        validProgram = GLES.makeProgram();

        //デプスバッファの有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // カリングの有効化
        GLES20.glEnable(GLES20.GL_CULL_FACE); //裏面を表示しないチェックを行う

        // 裏面を描画しない
        GLES20.glFrontFace(GLES20.GL_CCW); //表面のvertexのindex番号はCCWで登録
        GLES20.glCullFace(GLES20.GL_BACK); //裏面は表示しない

        //背景色の設定
        GLES20.glClearColor(0f, 0f, 0.2f, 1.0f);

        //光源の設定
        GLES.putLightAttribute(LightAmb, LightDif, LightSpc);

        // 背景とのブレンド方法を設定します。
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);    // 単純なアルファブレンド

        EarthPicture = new Texture(mContext,R.drawable.earthpicture); //テクスチャを作成
        MoonPicture = new Texture(mContext,R.drawable.moonpicture); //テクスチャを作成
    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 glUnused) {
        if (!validProgram) return;

        //画面のクリア
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
                GLES20.GL_DEPTH_BUFFER_BIT);

        //プロジェクション変換（射影変換）--------------------------------------
        //透視変換（遠近感を作る）
        //カメラは原点に有り，z軸の負の方向を向いていて，上方向はy軸＋方向である。
        GLES.gluPerspective(pMatrix,
                viewingangle,  //Y方向の画角
                aspect, //アスペクト比
                0.001f,   //ニアクリップ　　　z=-0.001から
                100.0f);//ファークリップ　　Z=-100までの範囲を表示することになる
        GLES.setPMatrix(pMatrix);

        //カメラビュー変換（視野変換）-----------------------------------
        //カメラ視点が原点になるような変換
        float[] c1Matrix=new float[16]; //カメラ視点変換マトリックス作成用
        float[] c2Matrix=new float[16]; //カメラ視点変換マトリックス作成用
        Matrix.setLookAtM(c1Matrix, 0,
                (float) (viewlength * Math.sin(beta) * Math.cos(alph)),  //カメラの視点 x
                (float) (viewlength * Math.sin(alph)),                    //カメラの視点 y
                (float) (viewlength * Math.cos(beta) * Math.cos(alph)),  //カメラの視点 z
                0.0f, 0.0f, 0.0f, //カメラの視線方向の代表点
                0.0f, 1.0f, 0.0f);//カメラの上方向
        Matrix.setIdentityM(c2Matrix, 0);
        Matrix.rotateM(c2Matrix, 0, -AngleHor, 0, 1, 0);
        Matrix.rotateM(c2Matrix, 0, -AngleVer, 1, 0, 0);
        Matrix.rotateM(c2Matrix, 0, -RotAngle, 0, 0, 1);
        Matrix.multiplyMM(cMatrix, 0, c2Matrix, 0, c1Matrix, 0); //cMatrix = c2Matrix * c1Matrix
        //カメラ視点ビュー変換はこれで終わり。
        GLES.setCMatrix(cMatrix);

        //cMatrixをセットしてから光源位置をセット
        GLES.setLightPosition(LightPos);

        GLES.selectProgram(GLES.SP_SimpleObject);

        //座標軸の描画
        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        //座標軸の描画本体
        MyAxes.draw();//座標軸の描画本体


        GLES.selectProgram(GLES.SP_SimpleObject);

        //球を描く(太陽)
        Matrix.setIdentityM(mMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.translateM(mMatrix,0,50f,0f,0f);
        Matrix.scaleM(mMatrix,0,SunRadius,SunRadius,SunRadius); //太陽の半径
        GLES.updateMatrix(mMatrix);//現在の変換行列をシェーダに指定
        m1SphereEX.draw(.98f,.98f,.9f,1f,5f);

        GLES.selectProgram(GLES.SP_TextureWithLight_Obstacle);
        GLES.setLightRadius(SunRadius); //太陽の半径

        //先の地球と月の変換行列を作成し位置を求めておく
        float[] EarthMatrix = new float[16];
        float[] MoonMatrix = new float[16];
        float[] EarthPos =new float[4];
        float[] MoonPos =new float[4];
        float[] ShadowColorOnEarth = {0f,0f,0f,1f};
        float[] ShadowColorOnMoon = {0.15f,0f,0f,1f};
        //地球
        Matrix.setIdentityM(EarthMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.rotateM(EarthMatrix,0,2f*angle,0f,1f,0f);
        Matrix.scaleM(EarthMatrix,0,EarthRadius,EarthRadius,EarthRadius);
        Matrix.multiplyMV(EarthPos, 0, EarthMatrix, 0, origin, 0);
        //月
        Matrix.setIdentityM(MoonMatrix, 0);//モデル変換行列mMatrixを単位行列にする。
        Matrix.rotateM(MoonMatrix,0,0.5f*angle,0f,1f,0f);
        Matrix.translateM(MoonMatrix,0,2f,0f,0f);
        Matrix.scaleM(MoonMatrix,0,MoonRadius,MoonRadius,MoonRadius);
        Matrix.multiplyMV(MoonPos, 0, MoonMatrix, 0, origin, 0);

        //テクスチャ付きの球を描く（地球）
        GLES.setObstaclePosition(MoonPos);
        GLES.setObstacleRadius(MoonRadius);
        GLES.setShadowColor(ShadowColorOnEarth);
        GLES.updateMatrix(EarthMatrix);//現在の変換行列をシェーダに指定
        EarthPicture.setTexture();
        m1TexSphereEX.draw(1f,1f,1f,1f,5f);

        //テクスチャ付きの球を描く（月）
        GLES.setObstaclePosition(EarthPos);
        GLES.setObstacleRadius(EarthRadius);
        GLES.setShadowColor(ShadowColorOnMoon);
        GLES.updateMatrix(MoonMatrix);//現在の変換行列をシェーダに指定
        MoonPicture.setTexture();
        m1TexSphereEX.draw(1f,1f,1f,1f,5f);


        angle+=0.5f*rotatatinvelocity;

    }

    public void setRotationVelocity(float rotv) {
        this.rotatatinvelocity=rotv;
    }

    //一本指でのスクロール
    private float Scroll[] = {0f, 0f}; //１本指のドラッグ[rad]
    public void setScrollValue(float DeltaX, float DeltaY) {
        Scroll[0] -= DeltaX * 0.01;
        if (3.14f < Scroll[0]) Scroll[0] = 3.14f;
        if (Scroll[0] < -3.14) Scroll[0] = -3.14f;
        Scroll[1] += DeltaY * 0.01;
        if (1.57f < Scroll[1]) Scroll[1] = 1.57f;
        if (Scroll[1] < -1.57) Scroll[1] = -1.57f;
        alph = Scroll[1];
        beta = Scroll[0];
    }


    //２本指でのスクロール
    private float Scroll2fg[] = {0f, 0f}; //２本指のドラッグ[deg]
    public void setScroll2Value(float Delta2X, float Delta2Y) {
        Scroll2fg[0] += Delta2X * 0.05; //[deg]
        Scroll2fg[1] += Delta2Y * 0.05; //[deg]
        AngleHor = Scroll2fg[0];
        AngleVer = Scroll2fg[1];
    }

    public void setScroll3Value(float Delta2X, float Delta2Y) {

    }

    //２本指でのピンチイン・ピンチアウト
    private float My_ScaleFactor = 1.0f;
    public void setPinch2(float factor) {
        My_ScaleFactor *= factor;
        viewlength = viewlength0 / My_ScaleFactor;
    }

    //３本指でのピンチイン・ピンチアウト
    private float My_ScaleFactor3 = 1.0f;
    public void setPinch3(float factor) {
        My_ScaleFactor3 *= factor;
        viewingangle = viewingangle0 / My_ScaleFactor3;
    }

    //２本指でのローテート
    private float My_RotAngle =0f;
    public void setRot2Angle(float Rotation) {
        My_RotAngle += Rotation;
        RotAngle = My_RotAngle;
    }

    public void setRot3Angle(float Rotation) {

    }

}
