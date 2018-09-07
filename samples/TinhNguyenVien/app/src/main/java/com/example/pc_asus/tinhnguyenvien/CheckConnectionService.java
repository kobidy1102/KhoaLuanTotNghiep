package com.example.pc_asus.tinhnguyenvien;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckConnectionService extends Service {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    public static  String keyRoomVideoChat;
    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


      //  mediaPlayer= MediaPlayer.create(this,R.raw.ringtone);
      //  mediaPlayer.start();
        Log.e("connect","start service...");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Status").child(uid).child("connectionRequest");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equalsIgnoreCase("0")==false){    // khac 0
                    Log.e("connect","kết nối đi nào...."+dataSnapshot.getValue().toString());                    //tới đây rồi,, giờ ,,, khi có yêu cầu thì làm cái như báo thức hỏi chấp nhận hoặc từ chối

                    keyRoomVideoChat= dataSnapshot.getValue().toString();
                  // sáng màn hình
//                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                    final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
//                            | PowerManager.ACQUIRE_CAUSES_WAKEUP
//                            | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
//                    wakeLock.acquire(30000); //30s sẽ tắt


                    Intent intent1 = new Intent(CheckConnectionService.this, HaveConnectionRequestActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }



}
