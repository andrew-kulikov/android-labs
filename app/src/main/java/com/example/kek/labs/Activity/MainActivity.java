package com.example.kek.labs.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kek.labs.BuildConfig;
import com.example.kek.labs.R;

public class MainActivity extends AppCompatActivity {
    private final int MY_REQUEST_CODE = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text1 = findViewById(R.id.text1);
        text1.setText(BuildConfig.VERSION_NAME + "   " + BuildConfig.VERSION_CODE);

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                showPermissionExplanation();
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
        } else
            setImeiText();
    }

    protected void setImeiText() {
        try {
            TextView textImei = findViewById(R.id.imeiTextView);
            textImei.setText(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        } catch (SecurityException e) {
            Log.e("Error", "PERMISSION DENIED");
        }
    }

    protected void showPermissionExplanation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permission_explanation_title);
        alertBuilder.setMessage(R.string.permission_explanation_content);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("permmm", String.valueOf(requestCode));
        Log.i("permmm", String.valueOf(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)));
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setImeiText();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))
                        showPermissionExplanation();
                    else
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Tag1", "OnPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Tag1", "On stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Tag", "Destroy");
    }
}
