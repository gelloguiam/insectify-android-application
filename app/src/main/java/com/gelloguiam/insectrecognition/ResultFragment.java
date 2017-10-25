package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ResultFragment extends Fragment {

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

            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openInsectWiki("title");
                }
            });

            result.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            result.setText(output.getTitle() + " (" + output.getConfidence() + ")");
            resultWrapper.addView(result);
        }
    }

    public void openInsectWiki(String insect) {
        Intent intent = new Intent(getActivity(), InsectWiki.class);
        startActivity(intent);
    }
}
