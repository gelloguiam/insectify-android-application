package com.gelloguiam.insectrecognition;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    static List<Classifier.Recognition> results;
    static Bitmap bitmap;
    static String hehe;
    private static CameraFragment cameraFragment;
    private static Fragment resultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        results = new ArrayList<>();

        cameraFragment = new CameraFragment();
        resultFragment = new ResultFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_wrapper, cameraFragment);
        fragmentTransaction.commit();
    }

    public static void showResultFragment(Activity activity) {
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        fragmentTransaction.replace(R.id.fragment_wrapper, resultFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
