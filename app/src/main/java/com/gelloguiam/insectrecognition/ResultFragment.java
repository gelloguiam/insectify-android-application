package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ResultFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        TextView text = (TextView) view.findViewById(R.id.textViewResult);
        text.setText(CameraActivity.results.toString());

        ImageView image =  (ImageView) view.findViewById(R.id.imageViewResult);
        image.setImageBitmap(CameraActivity.bitmap);

        return view;
    }

}
