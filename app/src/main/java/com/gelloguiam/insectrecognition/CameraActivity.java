package com.gelloguiam.insectrecognition;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    static Bitmap bitmap;
    static boolean resultsShown;
    static List<Classifier.Recognition> results;

    private static Fragment resultFragment;
    private static Fragment cameraFragment;
    private FragmentManager fragmentManager;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        results = new ArrayList<>();
        cameraFragment = new CameraFragment();
        resultFragment = new ResultFragment();
        fragmentManager = getFragmentManager();

        initializeTTS();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_wrapper, cameraFragment);
        fragmentTransaction.commit();
    }

    public static void showResultFragment(Activity activity) {
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.fragment_wrapper, resultFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        resultsShown = true;
    }

    protected void initializeTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "Language not supported.");
                    } else {
                        String text = "This is Insectify, your insect identification buddy.";
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                else {
                    Log.e("error", "Initilization failed.");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
            if(resultsShown) {
                resultsShown = false;
            }
            else {
                finish();
            }
        }
        else {
            finish();
        }
    }
}
