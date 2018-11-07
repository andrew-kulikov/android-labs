package com.example.kek.labs;

import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String versionName = BuildConfig.VERSION_NAME;
        TextView text1 = findViewById(R.id.text1);
        text1.setText(versionName + "   " + BuildConfig.VERSION_CODE);
    }
}
