package com.example.ech;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.ech.alarm.AlarmActivity;
import com.example.ech.call.CallActivity;
import com.example.ech.notes.NewNoteActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SwipeMenuActivity extends AppCompatActivity {
    GridLayout touchscreen;
    CardView card1;
    TextView t1;
    MediaPlayer mp;
    SpeechRecognizer speechrecog;
    Intent speechrecogintent;
    private long pressedTime;
    TextToSpeech textToSpeechhindi;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipemenu);
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        touchscreen=(GridLayout)findViewById(R.id.touchscreen);

        //to start the audio at the begining
        playmenu();
        textToSpeechhindi = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeechhindi.setLanguage(new Locale("hi","IN"));
                    textToSpeechhindi.setPitch(0.9f);
                    textToSpeechhindi.setSpeechRate(0.8f);
                }
            }
        });
        card1 = (CardView) findViewById(R.id.card1);
        t1=(TextView)findViewById(R.id.text1);
        //speech recognization code
        speechrecog=SpeechRecognizer.createSpeechRecognizer(this);
        speechrecogintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechrecogintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechrecogintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechrecog.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null)
                {

                    t1.setText(matches.get(0));
                    if (t1.getText().toString().equals("play again") ){
                        mp.start();
                    }
                    else if(t1.getText().toString().equals("more"))
                    {
                        Intent i = new Intent(SwipeMenuActivity.this,MoreMenuActivity.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        card1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if(!mp.isPlaying()) {
                            mp.start();
                        }
                        speechrecog.stopListening();
                        t1.setText(" Press and Speak ");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        if(mp.isPlaying()) {
                            mp.pause();
                        }
                        t1.setText("");
                        t1.setText(" Listening...");
                        speechrecog.startListening(speechrecogintent);
                        break;
                }
                return false;
            }
        });


    //Swipe Guestures methods
        touchscreen.setOnTouchListener(new OnSwipeTouchListener(SwipeMenuActivity.this) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onSwipeTop() {
                mp.pause();
                Calendar calendar = Calendar.getInstance();
                //Date
                SimpleDateFormat sdf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("EEEE dd-MMMM-YYYY");
                }
                String date = sdf.format(calendar.getTime());

                //Time
                int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);
                String strDate = "Time is :" + hour24hrs + "hour" + minutes +"minutes and"+ seconds +"seconds and Today is "+date+"";
                String hindate="समय है:"+hour24hrs+"घंटे "+minutes+" मिनट और "+seconds+" सेकंड और आजहै " +date+ "";

                if(lang.equals("hindi")) {
                    textToSpeechhindi.speak(hindate, TextToSpeech.QUEUE_FLUSH, null);
                }
                else
                {
                    textToSpeechhindi.speak(strDate, TextToSpeech.QUEUE_FLUSH, null);
                }

            }//Swipe up for time and date
            public void onSwipeRight() {
                mp.pause();
                Intent i = new Intent(SwipeMenuActivity.this, NewNoteActivity.class);
                i.putExtra("Language",lang);
                startActivity(i);
                finish();
            }//Swipe Right to open notes
            public void onSwipeLeft() {
                mp.pause();
                Intent i = new Intent(SwipeMenuActivity.this, CallActivity.class);
                i.putExtra("Language",lang);
                startActivity(i);
                finish();
            }//swipe left for call
            public void onSwipeBottom() {
                mp.pause();
                Intent i = new Intent(SwipeMenuActivity.this, AlarmActivity.class);
                i.putExtra("Language",lang);
                startActivity(i);
                finish();
            }//swipe bottom to open Alarm
        });
    }

    //media player
    public void playmenu()
    {   Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindiswipe);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.englishswipe);
                mp.start();
            }
        }
    }


    @Override
    protected void onUserLeaveHint()
    {
        mp.stop();
        super.onUserLeaveHint();
    }
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            mp.stop();
            super.onBackPressed();
            finish();
        } else {
            mp.stop();
            Intent intent = getIntent();
            String lang = intent.getExtras().getString("Language");
            if(lang.equals("hindi")) {
            textToSpeechhindi.speak("बाहर निकलने के लिए फिर से दबाएं", TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
            textToSpeechhindi.speak("Press back again to exit", TextToSpeech.QUEUE_FLUSH, null);
            }}
            pressedTime = System.currentTimeMillis();
    }

}