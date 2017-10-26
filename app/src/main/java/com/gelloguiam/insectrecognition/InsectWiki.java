package com.gelloguiam.insectrecognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InsectWiki extends AppCompatActivity {
    private static WebView wiki;
    private static final String wiki_link = "https://en.wikipedia.org/wiki/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_wiki);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        wiki = (WebView) findViewById(R.id.insect_wiki);
        wiki.loadUrl(wiki_link + name);
        wiki.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
