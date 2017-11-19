package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class CameraFragment extends Fragment {
    private Button openCamera;
    private Button openGallery;
    private CameraView cameraView;
    private ImageButton btnToggleCamera;
    private ImageButton btnDetectObject;
    private TextToSpeech tts;
    private static final int PICK_IMAGE_REQUEST = 1;

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

        cameraView = (CameraView) getFragmentManager()
            .findFragmentById(R.id.fragment_wrapper)
            .getView()
            .findViewById(R.id.live_camera);

        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP);
        
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                processImage(picture);
            }
        });

        btnToggleCamera = (ImageButton) getFragmentManager()
            .findFragmentById(R.id.fragment_wrapper)
            .getView()
            .findViewById(R.id.btnToggleCamera);

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject = (ImageButton) getFragmentManager()
            .findFragmentById(R.id.fragment_wrapper)
            .getView()
            .findViewById(R.id.btnDetectObject);

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeTTS();
                cameraView.captureImage();
            }
        });

        openGallery = (Button) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.open_gallery);
        openCamera = (Button) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.open_camera);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        fixLayoutSize();
        cameraView.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("activity", "HERE 1");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Log.i("activity", "HERE 3");
            Uri returnUri = data.getData();
            try {
                Log.i("activity", "HERE try");
                Bitmap image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] picture = stream.toByteArray();

                processImage(picture);
            } catch(Exception e) {
                Log.e("error", "Error loading image from gallery.");
            }
        }
    }

    public void processImage(byte[] picture) {
        Log.i("activity", "PROCESS");
        CameraActivity.bitmap = BitmapFactory.decodeByteArray(
                picture,
                0,
                picture.length);
        CameraActivity.bitmap = Bitmap.createScaledBitmap(
                CameraActivity.bitmap,
                MainActivity.INPUT_SIZE,
                MainActivity.INPUT_SIZE,
                false);
        CameraActivity.results = MainActivity.classifier.recognizeImage(CameraActivity.bitmap);
        CameraActivity.showResultFragment(getActivity());
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

        RelativeLayout captureButtonWrapper = (RelativeLayout) getFragmentManager()
            .findFragmentById(R.id.fragment_wrapper)
            .getView()
            .findViewById(R.id.capture_button_wrapper);
        LinearLayout galleryCameraToggle = (LinearLayout) getFragmentManager()
            .findFragmentById(R.id.fragment_wrapper)
            .getView()
            .findViewById(R.id.gallery_camera_toggle);

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

    protected void initializeTTS() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int result = tts.setLanguage(Locale.US);
                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("error", "Language not supported.");
                            } else {
                                String text = "Hold on, This might take a while.";
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else {
                            Log.e("error", "Initilization failed.");
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}
