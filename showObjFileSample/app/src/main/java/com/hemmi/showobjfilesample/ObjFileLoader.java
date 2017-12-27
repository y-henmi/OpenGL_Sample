package com.hemmi.showobjfilesample;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjFileLoader {
    private float vertex[];
    private float texcoord[];
    private float normals[];
    private short index[];
    private int indexCount;
    private boolean isLoad;

    //コンストラクタ
    ObjFileLoader(){
        indexCount = 0;
        isLoad = false;
    }
    //読み込みのメソッド。引数のscaleは大きさの指定です。
    public void FileLoad(InputStream in,float scale){
        InputStreamReader inst = new InputStreamReader(in);
        BufferedReader buf = new BufferedReader(inst);

        String bufstr;
        String[] buf_split = new String[4];
        float maxf = -scale;
        float minf = scale;
        short faceCnt = 0;
        List<Float> buf_vert = new ArrayList<Float>();
        List<Float> buf_texc = new ArrayList<Float>();
        List<Float> buf_norm = new ArrayList<Float>();
        List<String> buf_index = new ArrayList<String>();

        try {
            while((bufstr = buf.readLine()) != null){
                buf_split = bufstr.split(" ", 4);
                if(buf_split[0].equals("v")){
                    for(int i=1;i<4;i++){
                        buf_vert.add(Float.valueOf(buf_split[i]));
                        if(Float.valueOf(buf_split[i]) > maxf){
                            maxf = Float.valueOf(buf_split[i]);
                        }
                        if(Float.valueOf(buf_split[i]) < minf){
                            minf = Float.valueOf(buf_split[i]);
                        }
                    }
                }
                if(buf_split[0].equals("vt")){
                    for(int i=1;i<3;i++){
                        buf_texc.add(Float.valueOf(buf_split[i]));
                    }
                }
                if(buf_split[0].equals("vn")){
                    for(int i=1;i<4;i++){
                        buf_norm.add(Float.valueOf(buf_split[i]));
                    }
                }
                if(buf_split[0].equals("f")){
                    for(int i=1;i<4;i++){
                        buf_index.add(buf_split[i]);
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inst.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        indexCount = buf_index.size();
        index = new short[indexCount];
        for(int i=0;i<index.length;i++){
            index[i] = -1;
        }
        for(int i=0;i<buf_vert.size();i++){
            if((buf_vert.get(i) - minf) / (maxf - minf) > (scale/2.0f)){
                buf_vert.set(i, scale/2.0f);
            }else{
                buf_vert.set(i, ((buf_vert.get(i) - minf) / (maxf - minf)*scale)-(scale/2.0f));
            }
        }
        for(int i=0;i<buf_index.size();i++){
            if(index[i] < 0){
                String str = buf_index.get(i);
                for(int j=i;j<buf_index.size();j++){
                    if(str.equals(buf_index.get(j))) index[j] = faceCnt;
                }
                faceCnt++;
            }
        }
        vertex = new float[faceCnt*3];
        texcoord = new float[faceCnt*2];
        normals = new float[faceCnt*3];
        String bufstr_ss[] = new String[3];
        faceCnt = 0;
        for(int i=0;i<index.length;i++){
            if(index[i] == faceCnt){
                bufstr_ss = buf_index.get(i).split("/", 3);
                for(int j=0;j<3;j++){
                    vertex[faceCnt*3+j] = buf_vert.get((Integer.valueOf(bufstr_ss[0])-1)*3+j);
                }
                for(int j=0;j<2;j++){
                    if(j==0)texcoord[faceCnt*2+j] = buf_texc.get((Integer.valueOf(bufstr_ss[1])-1)*2+j); else texcoord[faceCnt*2+j] = 1f-buf_texc.get((Integer.valueOf(bufstr_ss[1])-1)*2+j);
                }
                for(int j=0;j<3;j++){
                    normals[faceCnt*3+j] = buf_norm.get((Integer.valueOf(bufstr_ss[2])-1)*3+j);
                }
                faceCnt++;
            }
        }
        isLoad = true;
    }

    public float[] getVertex(){
        if(!isLoad){
            Log.e("Rym_ObjFileLoader", "rym_VertexDataNothing");
        }
        return vertex;
    }
    public float[] getTexcoord(){
        if(!isLoad){
            Log.e("Rym_ObjFileLoader", "rym_TexcoordDataNothing");
        }
        return texcoord;
    }
    public float[] getNormals(){
        if(!isLoad){
            Log.e("Rym_ObjFileLoader", "rym_NormalsDataNothing");
        }
        return normals;
    }
    public short[] getIndex(){
        if(!isLoad){
            Log.e("Rym_ObjFileLoader", "rym_IndexDataNothing");
        }
        return index;
    }
    public int getIndexCount(){
        return indexCount;
    }
}
