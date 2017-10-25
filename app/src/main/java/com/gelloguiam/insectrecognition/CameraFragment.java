package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;


public class CameraFragment extends Fragment {

    private CameraView cameraView;
    private ImageButton btnToggleCamera;
    private ImageButton btnDetectObject;
    private Button openGallery;
    private Button openCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceType) {
        super.onActivityCreated(savedInstanceType);

        cameraView = (CameraView) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.live_camera);
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                CameraActivity.bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                CameraActivity.bitmap = Bitmap.createScaledBitmap(CameraActivity.bitmap,
                        MainActivity.INPUT_SIZE,
                        MainActivity.INPUT_SIZE, false);

                CameraActivity.results = MainActivity.classifier.recognizeImage(CameraActivity.bitmap);
                CameraActivity.showResultFragment(getActivity());
            }
        });

        btnToggleCamera = (ImageButton) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.btnToggleCamera);
        btnToggleCamera.setBackgroundColor(Color.TRANSPARENT);
        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject = (ImageButton) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.btnDetectObject);
        btnDetectObject.setBackgroundColor(Color.TRANSPARENT);
        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        openGallery = (Button) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.open_gallery);
        openCamera = (Button) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.open_camera);

        fixLayoutSize();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    public void fixLayoutSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        RelativeLayout captureButtonWrapper = (RelativeLayout) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.capture_button_wrapper);
        LinearLayout galleryCameraToggle = (LinearLayout) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.gallery_camera_toggle);

        ViewGroup.LayoutParams cameraViewParams = cameraView.getLayoutParams();
        cameraViewParams.height = screenWidth;
        cameraViewParams.width = screenWidth;
        cameraView.setLayoutParams(cameraViewParams);

        ViewGroup.LayoutParams galleryCameraToggleParams = galleryCameraToggle.getLayoutParams();
        galleryCameraToggleParams.height = 50;
        galleryCameraToggle.setLayoutParams(galleryCameraToggleParams);

        int captureWrapperHeight =  screenHeight - (screenWidth + 50 + 30);
        ViewGroup.LayoutParams captureButtonWrapperParams  = captureButtonWrapper.getLayoutParams();
        captureButtonWrapperParams.height = captureWrapperHeight;
        captureButtonWrapper.setLayoutParams(captureButtonWrapperParams);

        ViewGroup.LayoutParams toggleButtonParams  = openCamera.getLayoutParams();
        toggleButtonParams.width = screenWidth/2;

        openCamera.setLayoutParams(toggleButtonParams);
        openGallery.setLayoutParams(toggleButtonParams);
    }
}
