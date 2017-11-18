package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

public class ResultFragment extends Fragment {
    private TextToSpeech tts;

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

        final Classifier.Recognition output = CameraActivity.results.get(0);
        initializeTTS(output.getTitle(), Math.round(output.getConfidence() * 100));

        fixLayoutSize();
        renderResult();
    }

    public void fixLayoutSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
    }

    public void renderResult() {
        LinearLayout resultWrapper = (LinearLayout) getFragmentManager().
                findFragmentById(R.id.fragment_wrapper).
                getView().
                findViewById(R.id.results_wrapper);

        int resultsCount = CameraActivity.results.size();
        for(int i=0; i<resultsCount; i++) {
            final Classifier.Recognition output = CameraActivity.results.get(i);
            Button result = new Button(getActivity());

            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openInsectWiki(output.getTitle());
                }
            });

            float confidence = output.getConfidence() * 100;
            result.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            result.setText(output.getTitle() + " (" + confidence + ")");
            resultWrapper.addView(result);
        }
    }

    protected void initializeTTS(final String name, final int confidence) {
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    } else {
                        String text;
                        if(confidence < 30) {
                            text = "That image seems unrecognizable. Maybe I have to update my database.";
                        }
                        else if(confidence < 50) {
                            text = "Try to get a clearer image next time, but I have a guess that that is a " + name + "!";
                        } else {
                            text = "I am " + confidence + " percent sure that that is a " + name + "!";
                        }
                        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                    }
                }
                else {
                    Log.e("error", "Initilization Failed!");
                }
            }
        });
    }

    public void openInsectWiki(String insect) {
        Intent intent = new Intent(getActivity(), InsectWiki.class);
        intent.putExtra("name", insect);
        startActivity(intent);
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
