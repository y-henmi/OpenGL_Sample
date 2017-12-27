package com.hemmi.gles2sample04;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectUtil {

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureCoordinateBuffer;
    private FloatBuffer mNormalBuffer;
    private ShortBuffer mIndexBuffer;
    private int mNumIndexes;

    public void loadObject(Context context, int resourceId) {

        List<Float> vertexBuffer  = new ArrayList<>();   // 頂点座標
        List<Float> textureBuffer = new ArrayList<>();   // テクスチャ座標
        List<Float> normalBuffer  = new ArrayList<>();   // 法線
        List<String> indexeBuffer = new ArrayList<>();   // 頂点インデックス

        InputStream stream = context.getResources().openRawResource(resourceId);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        // .objファイルロード
        String line;
        String[] splitLine;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                splitLine = line.split(" ", 4);

                if (splitLine[0].equals("v")) {
                    for (int i = 1; i < 4; i++) {
                        vertexBuffer.add(Float.valueOf(splitLine[i]));
                    }
                }
                if (splitLine[0].equals("vt")) {
                    for (int i = 1; i < 3; i++) {
                        textureBuffer.add(Float.valueOf(splitLine[i]));
                    }
                }
                if (splitLine[0].equals("vn")) {
                    for (int i = 1; i < 4; i++) {
                        normalBuffer.add(Float.valueOf(splitLine[i]));
                    }
                }
                if (splitLine[0].equals("f")) {
                    for (int i = 1; i < 4; i++) {
                        indexeBuffer.add(splitLine[i]);
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bufferedReader.close();
            reader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        short faceCnt = 0;
        short[] index = new short[indexeBuffer.size()];
        for (int i = 0; i < index.length; i++) {
            index[i] = -1;
        }
        for (int i = 0; i < indexeBuffer.size(); i++) {
            String str = indexeBuffer.get(i);
            if (index[i] < 0) {
                for (int j = i; j < indexeBuffer.size(); j++) {
                    if (str.equals(indexeBuffer.get(j))) {
                        index[j] = faceCnt;
                    }
                }
                faceCnt++;
            }
        }

        float[] vertex  = new float[faceCnt * 3];
        float[] texture = new float[faceCnt * 2];
        float[] normal  = new float[faceCnt * 3];
        faceCnt = 0;
        for (int i = 0; i < index.length; i++) {
            if (index[i] == faceCnt) {
                splitLine = indexeBuffer.get(i).split("/", 3);
                for (int j = 0; j < 3; j++) {
                    // vertex
                    vertex[faceCnt * 3 + j] = vertexBuffer.get( (Integer.valueOf(splitLine[0]) - 1) * 3 + j );

                    // texture
                    if (j < 2) {
                        if (j == 0) {
                            texture[faceCnt * 2 + j] = textureBuffer.get( (Integer.valueOf(splitLine[1]) - 1) * 2 + j );
                        } else {
                            texture[faceCnt * 2 + j] = 1f - textureBuffer.get( (Integer.valueOf(splitLine[1]) - 1) * 2 + j );
                        }
                    }

                    // normals
                    normal[faceCnt * 3 + j] = normalBuffer.get( (Integer.valueOf(splitLine[2]) - 1) * 3 + j );
                }
                faceCnt++;
            }
        }

        // convert float buffer
        this.mVertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
        this.mVertexBuffer.position(0);

        this.mTextureCoordinateBuffer = ByteBuffer.allocateDirect(texture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texture);
        this.mTextureCoordinateBuffer.position(0);

        this.mNormalBuffer = ByteBuffer.allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(normal);
        this.mNormalBuffer.position(0);

        this.mIndexBuffer = ByteBuffer.allocate(index.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(index);
        this.mIndexBuffer.position(0);

        this.mNumIndexes = index.length;
    }

    public FloatBuffer getmVertexBuffer() {
        this.mVertexBuffer.position(0);
        return this.mVertexBuffer;
    }

    public FloatBuffer getTextureCoordinateBuffer() {
        this.mTextureCoordinateBuffer.position(0);
        return this.mTextureCoordinateBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        this.mNormalBuffer.position(0);
        return this.mNormalBuffer;
    }

    public ShortBuffer getIndexBuffer() {
        this.mIndexBuffer.position(0);
        return this.mIndexBuffer;
    }

    public int getNumIndexes() {
        return this.mNumIndexes;
    }
}
