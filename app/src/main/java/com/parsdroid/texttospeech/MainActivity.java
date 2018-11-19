package com.parsdroid.texttospeech;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Locale;

/*
 * Developed by R.hosseini
 * 11/4/2018
 */

public class MainActivity extends AppCompatActivity {

    private static final String TTS_TAG = "TTS";
    EditText mEditText;
    Button mButtonSpeak;
    TextToSpeech mTTS;
    SeekBar mSeekbarPitch;
    SeekBar mSeekbarSpeech;
    RadioGroup rgLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.editText);
        mButtonSpeak = findViewById(R.id.buttonSpeak);
        mSeekbarPitch = findViewById(R.id.seekBarPitch);
        mSeekbarSpeech = findViewById(R.id.seekBarSpeed);
        rgLanguage = findViewById(R.id.rg_language);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.ERROR) {
                    Log.e(TTS_TAG, "Initialize failed");
                } else {
                    int result = mTTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_NOT_SUPPORTED
                            || result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.e(TTS_TAG, "Language not supported");
                    }
                }
            }
        });

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    private void speak() {
        String text = mEditText.getText().toString();
        if (text.length() == 0) {
            Toast.makeText(this, "text is empty", Toast.LENGTH_SHORT).show();
        } else {
            float pitch = mSeekbarPitch.getProgress() / 50;
            if (pitch <= 0.1) pitch = 0.1f;
            mTTS.setPitch(pitch);

            float speed = mSeekbarSpeech.getProgress() / 50;
            if (speed <= 0.1) speed = 0.1f;
            mTTS.setSpeechRate(speed);

            int result;
            int selectedLanguageId = rgLanguage.getCheckedRadioButtonId();
            switch (selectedLanguageId) {
                default:
                case R.id.rb_english:
                    result = mTTS.setLanguage(Locale.ENGLISH);
                    break;
                case R.id.rb_french:
                    result = mTTS.setLanguage(Locale.FRANCE);
                    break;
                case R.id.rb_turkish:
                    result = mTTS.setLanguage(new Locale("tr", "TR"));
                    break;
            }

            if (result == TextToSpeech.LANG_NOT_SUPPORTED
                    || result == TextToSpeech.LANG_MISSING_DATA) {
                Toast.makeText(this, "this language not supported", Toast.LENGTH_SHORT).show();
            } else {
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}
