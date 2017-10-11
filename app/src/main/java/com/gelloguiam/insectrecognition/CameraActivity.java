package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.util.List;

public class CameraActivity extends AppCompatActivity {

    static List<Classifier.Recognition> results;
    static Bitmap bitmap;

    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;

    private ImageView imageViewResult;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

//        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
//        textViewResult = (TextView) findViewById(R.id.textViewResult);
//        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        cameraView = (CameraView) findViewById(R.id.cameraView);

        btnToggleCamera = (Button) findViewById(R.id.btnToggleCamera);
        btnDetectObject = (Button) findViewById(R.id.btnDetectObject);

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                bitmap = Bitmap.createScaledBitmap(bitmap,
                        MainActivity.INPUT_SIZE,
                        MainActivity.INPUT_SIZE, false);

                results = MainActivity.classifier.recognizeImage(bitmap);
                showResultFragment();

//                imageViewResult.setImageBitmap(bitmap);
//                textViewResult.setText(results.toString());
            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        cameraView.start();
    }

    public void showResultFragment() {
        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;

        Fragment fragment = new ResultFragment();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_wrapper, fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
