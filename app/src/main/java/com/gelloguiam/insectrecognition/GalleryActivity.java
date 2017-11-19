package com.gelloguiam.insectrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {
    private static int PICK_IMAGE_REQUEST = 1;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            initializeTTS();
            Uri returnUri = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), returnUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] picture = stream.toByteArray();
                CameraFragment.FROM_GALLERY = true;
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
                finish();
            } catch(Exception e) {
                Log.e("error", "Error loading image from gallery.");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void initializeTTS() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
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
