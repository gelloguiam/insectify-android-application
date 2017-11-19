package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.util.List;
import java.util.Locale;

public class CameraFragment extends Fragment {
    private ImageButton openGallery;
    private CameraView cameraView;
    private ImageButton btnToggleCamera;
    private ImageButton btnToggleFlash;
    private ImageButton btnDetectObject;
    private TextToSpeech tts;
    private boolean flashStatus = false;
    public static boolean FROM_GALLERY = false;

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

        btnToggleFlash = (ImageButton) getFragmentManager()
                .findFragmentById(R.id.fragment_wrapper)
                .getView()
                .findViewById(R.id.btnToggleFlash);

        btnToggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flashStatus) {
                    btnToggleFlash.setImageResource(R.drawable.flashoff);
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    flashStatus = false;
                }
                else {
                    btnToggleFlash.setImageResource(R.drawable.flashon);
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    flashStatus = true;
                }
            }
        });

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

        openGallery = (ImageButton) getFragmentManager().findFragmentById(R.id.fragment_wrapper).getView().findViewById(R.id.open_gallery);
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        fixLayoutSize();
        cameraView.start();
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
        CameraActivity.bitmap = ImageHelper.getRotatedImage(CameraActivity.bitmap, 90);
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
        if(FROM_GALLERY) {
            List<Classifier.Recognition> results = MainActivity.classifier.recognizeImage(CameraActivity.bitmap);
            if(results.isEmpty()) return;
            CameraActivity.showResultFragment(getActivity());
            FROM_GALLERY = false;
        }
        else {
            if(flashStatus) {
                btnToggleFlash.setImageResource(R.drawable.flashon);
                cameraView.setFlash(CameraKit.Constants.FLASH_ON);
            }
            else {
                btnToggleFlash.setImageResource(R.drawable.flashoff);
                cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
            }
            cameraView.start();
        }
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

        ViewGroup.LayoutParams cameraViewParams = cameraView.getLayoutParams();
        cameraViewParams.height = screenWidth;
        cameraViewParams.width = screenWidth;
        cameraView.setLayoutParams(cameraViewParams);

        int captureWrapperHeight =  screenHeight - (screenWidth + 50 + 30);
        ViewGroup.LayoutParams captureButtonWrapperParams  = captureButtonWrapper.getLayoutParams();
        captureButtonWrapperParams.height = captureWrapperHeight;
        captureButtonWrapper.setLayoutParams(captureButtonWrapperParams);
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
