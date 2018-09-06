package com.example.pc_asus.tinhnguyenvien;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("connect","start service...");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("TinhNguyenVien").child("Status").child(uid).child("connectionRequest");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equalsIgnoreCase("0")==false){    // khac 0
                    Log.e("connect","kết nối đi nào...."+dataSnapshot.getValue().toString());                    //tới đây rồi,, giờ ,,, khi có yêu cầu thì làm cái như báo thức hỏi chấp nhận hoặc từ chối
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }
}
