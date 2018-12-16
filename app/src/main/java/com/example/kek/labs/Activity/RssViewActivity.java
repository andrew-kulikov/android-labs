package com.example.kek.labs.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.kek.labs.R;

public class RssViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_view);

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");
        WebView webView = findViewById(R.id.rss_web_view);
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(link);
    }
}
