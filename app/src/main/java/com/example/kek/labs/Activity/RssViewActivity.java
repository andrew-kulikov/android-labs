package com.example.kek.labs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.kek.labs.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class RssViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rss_view);

        Intent intent = getIntent();
        String link = intent.getStringExtra("link");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        WebView webView = findViewById(R.id.rss_web_view);
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(link);
    }
}
