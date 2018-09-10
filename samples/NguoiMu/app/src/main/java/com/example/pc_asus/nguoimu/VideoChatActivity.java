package com.example.pc_asus.nguoimu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class VideoChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,TextToSpeech.OnInitListener {
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    String uid;
    boolean readData=false;
    private  boolean calling=false;
    String idSelected;
    TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    TextView tv2;
    Button btn_dangXuat,btn_thongTinTaiKhoan;
    private static final String LOG_TAG = VideoChatViewActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_video_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView= navigationView.getHeaderView(0);
        final ImageView img= (ImageView) headView.findViewById(R.id.img_bar_avatar);
        final TextView tv_name=(TextView) headView.findViewById(R.id.tv_bar_name);
        final String[] photoURL = new String[1];


        final View tvTap =  findViewById(R.id.tv_tap);




        tts= new TextToSpeech(this, this);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
         uid= mCurrentUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("NguoiMu").child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_name.setText(dataSnapshot.child("name").getValue().toString());
                photoURL[0] =dataSnapshot.child("photoURL").getValue().toString();
               // Picasso.with(VideoChatActivity.this).load(dataSnapshot.child("photoURL").getValue().toString()).into(img);
                RequestOptions  requestOptions = new RequestOptions();
                requestOptions.fitCenter();
                requestOptions.placeholder(R.mipmap.user);
                Glide.with(getApplicationContext())
                        .load(photoURL[0])
                        .apply(requestOptions)
                        //   .override(200,150)
                        .into(img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        tvTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
//                readData=true;
//                getListFriend(uid);
            }
        });



    }



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

                            if (status == 1 && status1==0) {
                                arrTNVFreeTime.add(dataSnapshot.getKey());
                                Log.e("arr", dataSnapshot.getKey());

                            }

                            if (finished == arr2.size() - 1) {

                                if(arr2.size()!=0) {
                                    Random rd = new Random();
                                    int number = rd.nextInt(arrTNVFreeTime.size());
                                    idSelected = arrTNVFreeTime.get(number);
                                    Toast.makeText(VideoChatActivity.this, idSelected, Toast.LENGTH_SHORT).show();//////////////ĐÃ chọn dc bbe đang rãnh
                                    mDatabase.child("TinhNguyenVien").child("Status").child(idSelected).child("connectionRequest").setValue(uid);                           // nhớ sữa code xet lun coi có đang kết nối vs ai ko mới chọn


                                    mDatabase.child("TinhNguyenVien").child("Status").child(idSelected).child("connectionRequest").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String disconnect= dataSnapshot.getValue().toString();
                                            if(disconnect.equalsIgnoreCase("0")){
                                                tts.speak("dang kết nối lại", TextToSpeech.QUEUE_FLUSH,null);
                                                initAgoraEngineAndJoinChannel();
                                                readData=true;
                                                getListFriend(uid);
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






// Của Layout








    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_accountSetting) {
            startActivity(new Intent(VideoChatActivity.this,AccountSettingsActivity.class));

        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(VideoChatActivity.this,FriendsActivity.class));

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(VideoChatActivity.this,SignInActivity.class));
            finish();

        }else if (id == R.id.nav_search) {
            startActivity(new Intent(VideoChatActivity.this,SearchTnvActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }







    ////của VideoCall




    private void initAgoraEngineAndJoinChannel() {
        calling=true;
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

        if(calling==true) {
            leaveChannel();
            RtcEngine.destroy();                  //chưa gọi nên khi thoát màn hình nó chưa có đối tượng
            mRtcEngine = null;
            calling=false;
        }
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



    private void promptSpeechInput() {
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

                        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
                           tts.speak("đang kết nối, vui lòng chờ", TextToSpeech.QUEUE_FLUSH,null);
                            initAgoraEngineAndJoinChannel();
                            mRtcEngine.switchCamera();
  ///////                          ///////////////////////////////////
                            readData=true;
                            getListFriend(uid);



                        }




                    }


                }
            }
//            else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
//                showToastMessage("Audio Error");
//            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
//                showToastMessage("Client Error");
//            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
//                showToastMessage("Network Error");
//            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
//                showToastMessage("No Match");
//            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
//                showToastMessage("Server Error");
//            }


        }


    }
}
