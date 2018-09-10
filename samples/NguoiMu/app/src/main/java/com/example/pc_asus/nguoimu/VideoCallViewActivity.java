package com.example.pc_asus.nguoimu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


public class VideoCallViewActivity extends AppCompatActivity implements  TextToSpeech.OnInitListener{
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    String uid;
    boolean readData=false;
    String idSelected;
    TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Button btn_dangXuat;
    private static final String LOG_TAG = VideoCallViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private RtcEngine mRtcEngine;// Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) { // Tutorial Step 5
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) { // Tutorial Step 10
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }
    };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_video_call_view);
            tts= new TextToSpeech(this, this);

            mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
            uid= mCurrentUser.getUid();
            mDatabase= FirebaseDatabase.getInstance().getReference();

            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
               // tts.speak("đang kết nối, vui lòng chờ", TextToSpeech.QUEUE_FLUSH,null);
                initAgoraEngineAndJoinChannel();
                mRtcEngine.switchCamera();
                readData=true;
                getListFriend(uid);
            }

            TextView tv_endCall= findViewById(R.id.tv_endCallActivity);
            tv_endCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.child("TinhNguyenVien").child("Status").child(idSelected).child("connectionRequest").setValue(0);
                    finish();
                }
            });

        }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        setupVideoProfile();         // Tutorial Step 2
        // setupLocalVideo();           // Tutorial Step 3
        joinChannel();               // Tutorial Step 4

    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(android.Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA);
                } else {
                    showLongToast("No permission for " + android.Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
            case PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + android.Manifest.permission.CAMERA);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

            leaveChannel();
            RtcEngine.destroy();                  //chưa gọi nên khi thoát màn hình nó chưa có đối tượng
            mRtcEngine = null;

        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_480P_10, false);
    }


    // Tutorial Step 4
    private void joinChannel() {
        mRtcEngine.joinChannel(null, uid, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceView.setTag(uid); // for mark purpose
    }

    // Tutorial Step 6
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Tutorial Step 7
    private void onRemoteUserLeft() {
    }

    // Tutorial Step 10
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
    }

    @Override
    public void onInit(int i) {
        if(i !=TextToSpeech.ERROR) {

            Locale l = new Locale("vi");
            tts.setLanguage(l);

        }
    }



    private void promptSpeechInput() {           //mở đialog nói
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // xac nhan ung dung muon gui yeu cau
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // goi y nhung dieu nguoi dung muon noi

        // goi y nhan dang nhung gi nguoi dung se noi
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something…");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Trả lại dữ liệu sau khi nhập giọng nói vào
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT:
        if( requestCode==REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // nói lại những gì vừa nghe dc

                for(int i=0;i<result.size();i++) {
                    Log.e("abc", result.get(i));
                    if(result.get(i).equalsIgnoreCase("kết nối")){






                    }


                }
            }


        }


    }





    // hàm tính toán
    private void getListFriend(String uid){

        final ArrayList<String> arr = new ArrayList<String>();

        mDatabase.child("NguoiMu").child("Friends").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(readData==true) {
                    arr.add(dataSnapshot.getKey());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mDatabase.child("NguoiMu").child("Friends").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(readData==true) {
                    getStatusOfFriends(arr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    private void getStatusOfFriends(final ArrayList<String> arr2){

        final ArrayList<String> arrTNVFreeTime = new ArrayList<String>();

        for (int i = 0; i < arr2.size(); i++) {
            Log.e("arr", "friends=" + arr2.get(i));
            final int finished = i;
            mDatabase.child("TinhNguyenVien").child("Status").child(arr2.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(readData==true) {
                        String s0 = dataSnapshot.child("statusWithFriends").getValue().toString();
                        int status = Integer.parseInt(s0);
                        String s1 = dataSnapshot.child("connectionRequest").getValue().toString();
                        int status1 = Integer.parseInt(s1);

                        if ((status == 1) && (status1==0 || status1==1)) {
                            arrTNVFreeTime.add(dataSnapshot.getKey());
                            Log.e("arr", dataSnapshot.getKey());

                        }

                        if (finished == arr2.size() - 1) {

                            if(arr2.size()!=0) {
                                Random rd = new Random();
                                int number = rd.nextInt(arrTNVFreeTime.size());
                                idSelected = arrTNVFreeTime.get(number);
                                Toast.makeText(VideoCallViewActivity.this, idSelected, Toast.LENGTH_SHORT).show();//////////////ĐÃ chọn dc bbe đang rãnh
                                mDatabase.child("TinhNguyenVien").child("Status").child(idSelected).child("connectionRequest").setValue(uid);                           // nhớ sữa code xet lun coi có đang kết nối vs ai ko mới chọn


                                // nếu TNV ko bắt máy thì sẽ kết nối lại vs TNV  random
                                mDatabase.child("TinhNguyenVien").child("Status").child(idSelected).child("connectionRequest").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String disconnect= dataSnapshot.getValue().toString();
                                        if(disconnect.equalsIgnoreCase("1")){
                                          //  tts.speak("dang kết nối lại", TextToSpeech.QUEUE_FLUSH,null);
                                            initAgoraEngineAndJoinChannel();
                                            readData=true;
                                            getListFriend(uid);
                                        }else if(disconnect.equalsIgnoreCase("0")){
                                            tts.speak("đã ngắt kết nối", TextToSpeech.QUEUE_FLUSH,null);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //CODE
                                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
                                    initAgoraEngineAndJoinChannel();
                                }

                                Log.e("arr", idSelected);
                                readData = false;
                            }else{                                      // bạn bè ko có ai rãnh thì kết nối vs người lạ

                                ////
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }
    }