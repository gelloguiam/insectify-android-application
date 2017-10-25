package com.gelloguiam.insectrecognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class InsectWiki extends AppCompatActivity {
    private static WebView wiki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_wiki);
        wiki = (WebView) findViewById(R.id.insect_wiki);
        wiki.loadUrl("http://www.google.com");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
