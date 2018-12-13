package com.parsdroid.texttospeech;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
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
    private static final String GOOGLE_TTS_PACKAGE = "com.google.android.tts";
    EditText mEditText;
    Button mButtonSpeak;
    TextToSpeech mTTS;
    SeekBar mSeekBarPitch;
    SeekBar mSeekBarSpeed;
    RadioGroup rgLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.editText);
        mButtonSpeak = findViewById(R.id.buttonSpeak);
        mSeekBarPitch = findViewById(R.id.seekBarPitch);
        mSeekBarSpeed = findViewById(R.id.seekBarSpeed);
        rgLanguage = findViewById(R.id.rg_language);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.ERROR) {
                    Log.e(TTS_TAG, "Initialize failed");
                    openTTSEngineDownloadDialog();
                } else {
                    if (isPackageInstalled(GOOGLE_TTS_PACKAGE)) {
                        mTTS.setEngineByPackageName(GOOGLE_TTS_PACKAGE);
                    }
                    int result = mTTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_NOT_SUPPORTED
                            || result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.e(TTS_TAG, "language not supported");
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

    private void openTTSEngineDownloadDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Download Google TTS");
        builder.setMessage("Your device does'nt have text-to-speech capabilities, or disabled. " +
                "Do you want to add this feature to your device? " +
                "Download Google TTS (or enable it).");

        //This will not allow to close builder until user selects an option
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TTS_TAG, "TTSEngineDownloadDialog: positive button pressed");
                downloadGoogleTTS();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TTS_TAG, "TTSEngineDownloadDialog: negative button pressed");
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void downloadGoogleTTS() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + GOOGLE_TTS_PACKAGE)));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + GOOGLE_TTS_PACKAGE)));
        }
    }

    private void speak() {
        String text = mEditText.getText().toString();
        if (text.length() == 0) {
            Toast.makeText(this, "Text is empty", Toast.LENGTH_SHORT).show();
        } else {
            float pitch = mSeekBarPitch.getProgress() / 50;
            if (pitch <= 0.1) pitch = 0.1f;
            mTTS.setPitch(pitch);

            float speed = mSeekBarSpeed.getProgress() / 50;
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
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
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