package com.gelloguiam.insectrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Mul";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";

    static Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeModel();
    }

    private void initializeModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                    startCameraActivity();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow Model!", e);
                }
            }
        }).start();
    }

    private void startCameraActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        }).start();
        finish();
    }

//    private void animatePreloader() {
//        final ImageView preloader = (ImageView) findViewById(R.id.preloader);
//        final LinkedList<Drawable> animation = new LinkedList<Drawable>();
//        animation.add(getResources().getDrawable(R.drawable.g1));
//        animation.add(getResources().getDrawable(R.drawable.g2));
//        animation.add(getResources().getDrawable(R.drawable.g3));
//        animation.add(getResources().getDrawable(R.drawable.g4));
//        animation.add(getResources().getDrawable(R.drawable.g5));
//        animation.add(getResources().getDrawable(R.drawable.g0));
//
//        runOnUiThread(new Runnable(){
//
//            @Override
//            public void run() {
//                int count = 0;
//
//                while(true) {
//
//                    count++;
//                    if(count >= animation.size()) count = count % animation.size();
//                    preloader.setImageDrawable(animation.get(count));
//
//                    try{
//                        Thread.sleep(1000);
//                    } catch(Exception e) {}
//                }
//
//            }
//
//        });
//    }

}
