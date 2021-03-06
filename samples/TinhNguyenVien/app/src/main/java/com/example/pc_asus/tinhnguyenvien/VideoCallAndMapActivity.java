package com.example.pc_asus.tinhnguyenvien;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VideoCallAndMapActivity extends AppCompatActivity {
    public static String key;


    LinearLayout ll_VideoCall;
    LinearLayout ll_Map;

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        Log.e("abc","videocall and map key "+key);

        setContentView(R.layout.activity_video_call_and_map);




        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

       final ImageView imgScroll= findViewById(R.id.img_scroll);
        ll_VideoCall= findViewById(R.id.ll_video_call);
        ll_Map= findViewById(R.id.ll_map);
       final RelativeLayout rlMatchParent= findViewById(R.id.rl_matchparent);

      final LinearLayout ll_VideoCall2= findViewById(R.id.ll_video_call2);

       imgScroll.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               Log.e("abc", "x:" + event.getRawX() + " y:" + event.getRawY());
               ll_VideoCall.setLayoutParams(new LinearLayout.LayoutParams(rlMatchParent.getWidth(), ((int) event.getRawY()-50)));
               ll_Map.setLayoutParams(new LinearLayout.LayoutParams(rlMatchParent.getWidth(), rlMatchParent.getHeight()-ll_VideoCall.getHeight()));

               ll_VideoCall2.setLayoutParams(new LinearLayout.LayoutParams(rlMatchParent.getWidth(), ((int) event.getRawY())-50-50));

               return true;
           }
       });





    }



}
