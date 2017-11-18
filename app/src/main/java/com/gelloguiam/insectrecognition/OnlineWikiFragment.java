package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class OnlineWikiFragment extends Fragment {
    private static WebView wiki;
    private static final String wiki_link = "https://en.wikipedia.org/wiki/";

    public OnlineWikiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_wiki, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceType) {
        super.onActivityCreated(savedInstanceType);

        wiki =(WebView) getFragmentManager().
                findFragmentById(R.id.wiki_wrapper).
                getView().
                findViewById(R.id.insect_wiki);

        wiki.loadUrl(wiki_link + InsectWiki.subject);
        wiki.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
