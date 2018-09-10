package com.example.pc_asus.tinhnguyenvien;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HaveConnectionRequestActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private ImageView imgAvatar, imgStartCall, imgEndCall;
    private TextView tvName;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private int checkStartCall=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_connection_request);

        //activity hiển thị lên trước màn hình khóa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mediaPlayer= MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.start();

        //rung 0-> vô thời hạn, rung 0,4s nghĩ 1s .
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 400, 1300};
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          //  v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        }else{
            //deprecated in API 26
            vibrator.vibrate(pattern, 0);
        }



        checkStartCall=1;

        imgAvatar= findViewById(R.id.img_connect_avatar);
        imgStartCall= findViewById(R.id.img_startCall);
        imgEndCall= findViewById(R.id.img_endCall);
        tvName= findViewById(R.id.tv_connect_name);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        mDatabase.child("NguoiMu").child("Users").child(CheckConnectionService.keyRoomVideoChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("abc",""+CheckConnectionService.keyRoomVideoChat);
                String name= dataSnapshot.child("name").getValue().toString();
                String avatarLink= dataSnapshot.child("photoURL").getValue().toString();

                tvName.setText(name);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.fitCenter();
                requestOptions.placeholder(R.mipmap.user);
                Glide.with(getApplicationContext())
                        .load(avatarLink)
                        .apply(requestOptions)
                        //   .override(200,150)
                        .into(imgAvatar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imgEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HaveConnectionRequestActivity.this, "click", Toast.LENGTH_SHORT).show();
                //bận
                mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("statusWithFriends").setValue(0);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("statusWithFriends").setValue(1);

                    }
                }, 20000);

                mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("connectionRequest").setValue(1);
                mediaPlayer.stop();
                vibrator.cancel();
                finish();


//                WindowManager.LayoutParams params = getWindow().getAttributes();
//                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//                params.screenBrightness = -1;
//                getWindow().setAttributes(params);

            }
        });


        imgStartCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkStartCall=2;

                startActivity(new Intent(HaveConnectionRequestActivity.this,VideoCallActivity.class));

                mediaPlayer.stop();
                vibrator.cancel();
                finish();
            }
        });


        //35s mà ko nhấc máy thì hủy

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(checkStartCall==1){
                    Toast.makeText(HaveConnectionRequestActivity.this, "35s", Toast.LENGTH_SHORT).show();
                    mDatabase.child("TinhNguyenVien").child("Status").child(uid).child("connectionRequest").setValue(1);
                    mediaPlayer.stop();
                    vibrator.cancel();
                    finish();
                }
                }
        }, 20000);


    }
}
