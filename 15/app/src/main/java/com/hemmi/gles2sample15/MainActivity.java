package com.hemmi.gles2sample15;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    private MyGLSurfaceView glView;
    //private GestureDetector gesDetector = null;

    public View mTableLayout;

    private Button btnleft;
    private Button btnstop;
    private Button btnright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GLサーフェスビュー
        glView=(MyGLSurfaceView)findViewById(R.id.id_myGLview);
        //gesDetector = new GestureDetector(this, (GestureDetector.OnGestureListener) glView);

        //上面のtablelayoutを取得
        mTableLayout=(View)findViewById(R.id.UpperLayout);
        glView.setUpperLayout(mTableLayout);

        //buttonを取得
        btnleft = (Button)findViewById(R.id.b_left);
        btnleft.setOnClickListener(clicked);
        btnstop = (Button)findViewById(R.id.b_stop);
        btnstop.setOnClickListener(clicked);
        btnright = (Button)findViewById(R.id.b_right);
        btnright.setOnClickListener(clicked);
    }

    private View.OnClickListener clicked = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.b_left:
                    glView.setRotationVelocity(-1f);
                    break;
                case R.id.b_stop:
                    glView.setRotationVelocity(0f);
                    break;
                case R.id.b_right:
                    glView.setRotationVelocity(1f);
                    break;
            }
            mTableLayout.setVisibility(View.INVISIBLE);
        }
    };

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event){
        return gesDetector.onTouchEvent(event);
    }
    */

    //アクティビティレジューム時に呼ばれる
    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
    }

    //アクティビティポーズ時に呼ばれる
    @Override
    public void onPause() {
        super.onPause();
        glView.onPause();
    }


}
