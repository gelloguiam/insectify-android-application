package com.gelloguiam.insectrecognition;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    static Bitmap bitmap;
    static boolean resultsShown;
    static List<Classifier.Recognition> results;

    private static Fragment resultFragment;
    private static Fragment cameraFragment;
    private FragmentManager fragmentManager;

    int IMAGE_PICKER_SELECT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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
