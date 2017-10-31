package com.gelloguiam.insectrecognition;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    static Bitmap bitmap;
    static boolean resultsShown;
    static List<Classifier.Recognition> results;
    static TextToSpeech agent;

    private static Fragment resultFragment;
    private static Fragment cameraFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        agent = new TextToSpeech(getApplication(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    agent.setLanguage(Locale.US);
                }
            }
        });

        String text = "Hello World!, The results are the following.";
        agent.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        results = new ArrayList<>();

        cameraFragment = new CameraFragment();
        resultFragment = new ResultFragment();
        fragmentManager = getFragmentManager();

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

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back Pressed!." + fragmentManager.getBackStackEntryCount(), Toast.LENGTH_LONG).show();

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
