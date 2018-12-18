package com.example.kek.labs.Activity;

import android.os.Bundle;

import com.example.kek.labs.Managers.FileManager;
import com.example.kek.labs.R;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FileManager.createDirs();
    }
}
