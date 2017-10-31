package com.gelloguiam.insectrecognition;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InsectWiki extends AppCompatActivity {
    public static String subject;
    private static Fragment localWiki;
    private static Fragment onlineWiki;
    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insect_wiki);

        Bundle bundle = getIntent().getExtras();
        subject = bundle.getString("name");

        localWiki = new LocalWikiFragment();
        onlineWiki = new OnlineWikiFragment();
        fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.wiki_wrapper, localWiki);
        fragmentTransaction.commit();
    }

    public static void openWebWiki() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiki_wrapper, onlineWiki);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
