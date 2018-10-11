package com.example.pc_asus.nguoimu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class OpenAppWithVoice extends AppCompatActivity implements TextToSpeech.OnInitListener {

    int REQ_CODE_SPEECH_INPUT2 = 0511;
    TextView tvOpenApp;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_app_with_voice);
        tvOpenApp = (TextView)findViewById(R.id.tv_open_app);

        tts = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak("Nhấp vào màn hình nói Có để mở ứng dụng, nói không để thoát", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 1000);

        tvOpenApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    public void onInit(int i) {
        if(i !=TextToSpeech.ERROR) {

            Locale l = new Locale("vi");
            tts.setLanguage(l);

        }
    }
    private void promptSpeechInput() {
    //    tts.speak("Bạn có muốn mở ứng dụng không", TextToSpeech.QUEUE_FLUSH, null);
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
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT2);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT:
        if( requestCode==REQ_CODE_SPEECH_INPUT2) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // nói lại những gì vừa nghe dc

                for (int i = 0; i < result.size(); i++) {
                    Log.e("abc", result.get(i));
                    if (result.get(i).equalsIgnoreCase("có")) {
                        try {
                            tts.speak("Chạm vào màn hình để ra lệnh", TextToSpeech.QUEUE_FLUSH, null);
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(OpenAppWithVoice.this, MainActivity.class));
                        finish();
                    }
                    if (result.get(i).equalsIgnoreCase("không")) {
                        try {
                            tts.speak("Đã thoát ứng dụng", TextToSpeech.QUEUE_FLUSH, null);
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }
                }


            }


        }
    }
}
