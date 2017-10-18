package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

public class ResultFragment extends Fragment {
    TextToSpeech agent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceType) {
        super.onActivityCreated(savedInstanceType);

        agent = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    agent.setLanguage(Locale.US);
                }
                else {
                    Toast.makeText(getActivity(), "Text to speech not initialized.", Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageView image =  (ImageView) getFragmentManager()
            .findFragmentById(R.id.fragment_wrapper)
            .getView()
            .findViewById(R.id.image_captured_preview);
        image.setImageBitmap(CameraActivity.bitmap);

        fixLayoutSize();
        renderResult();
    }

    public void fixLayoutSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        FrameLayout previewWrapper = (FrameLayout) getFragmentManager().
                findFragmentById(R.id.fragment_wrapper).
                getView().
                findViewById(R.id.image_captured_preview_wrapper);

        ViewGroup.LayoutParams previewWrapperParams = previewWrapper.getLayoutParams();
        previewWrapperParams.height = screenWidth;
        previewWrapperParams.width = screenWidth;
        previewWrapper .setLayoutParams(previewWrapperParams);
    }

    public void renderResult() {
        LinearLayout resultWrapper = (LinearLayout) getFragmentManager().
                findFragmentById(R.id.fragment_wrapper).
                getView().
                findViewById(R.id.results_wrapper);

        int resultsCount = CameraActivity.results.size();
        for(int i=0; i<resultsCount; i++) {
            Classifier.Recognition output = CameraActivity.results.get(i);
            Button result = new Button(getActivity());
            result.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            result.setText(output.getTitle() + " (" + output.getConfidence() + ")");
            resultWrapper.addView(result);
        }
        
//        String text = "Hello World!, The results are the following.";
//        agent.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }
}
