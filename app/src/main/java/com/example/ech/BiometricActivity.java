        package com.example.ech;

        import androidx.annotation.NonNull;
        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.cardview.widget.CardView;
        import androidx.core.content.ContextCompat;
        import androidx.biometric.BiometricManager;
        import androidx.biometric.BiometricPrompt;
        import java.util.concurrent.Executor;

        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.speech.tts.TextToSpeech;
        import android.view.View;
        import android.widget.TextView;
        import java.util.Locale;

        public class BiometricActivity extends AppCompatActivity {
            CardView card1;
            TextView t1;
            MediaPlayer mp;
            private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

            long lastClickTime = 0;
            private long pressedTime;
            TextToSpeech textToSpeechhindi;
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_biometric);
                playmenu();
                textToSpeechhindi = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i != TextToSpeech.ERROR) {
                            // To Choose language of speech
                            textToSpeechhindi.setLanguage(new Locale("hi", "IN"));
                            textToSpeechhindi.setPitch(0.8f);
                            textToSpeechhindi.setSpeechRate(1.1f);
                        }
                    }
                });
                    card1 = (CardView) findViewById(R.id.card1);
                    t1 = (TextView) findViewById(R.id.text1);
                androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
                switch (biometricManager.canAuthenticate()) {

                    // this means we can use biometric sensor
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        t1.setText("You can use the fingerprint sensor to login");
                        break;

                    // this means that the device doesn't have fingerprint sensor
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        t1.setText("This device doesnot have a fingerprint sensor");
                        card1.setVisibility(View.GONE);
                        break;

                    // this means that biometric sensor is not available
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        t1.setText("The biometric sensor is currently unavailable");
                        card1.setVisibility(View.GONE);
                        break;

                    // this means that the device doesn't contain your fingerprint
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        t1.setText("Your device doesn't have fingerprint saved,please check your security settings");
                        card1.setVisibility(View.GONE);
                        break;
                }

                Executor executor = ContextCompat.getMainExecutor(this);
                final BiometricPrompt biometricPrompt = new BiometricPrompt(BiometricActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        textToSpeechhindi.speak("Login Successfull",TextToSpeech.QUEUE_FLUSH,null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(BiometricActivity.this, LangActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, 2000);

                    }
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });
                final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Authenticate Yourself")
                        .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
                    card1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!mp.isPlaying()){
                                mp.start();
                            }
                            else{
                                mp.pause();
                            }
                            long clickTime = System.currentTimeMillis();
                            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                                mp.pause();
                                biometricPrompt.authenticate(promptInfo);
                                lastClickTime = 0;
                            }
                            lastClickTime = clickTime;

                            }
                        });
            }
                public void playmenu() {
                    if (mp == null) {
                        mp = MediaPlayer.create(this, R.raw.biometric);
                        mp.start();
                    }
                }
            @Override
            protected void onUserLeaveHint() {
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
                    textToSpeechhindi.speak("Press back again to exit", TextToSpeech.QUEUE_FLUSH, null);
                }
                pressedTime = System.currentTimeMillis();
            }
        }