package com.example.kek.labs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private final int MY_REQUEST_CODE = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text1 = findViewById(R.id.text1);
        text1.setText(BuildConfig.VERSION_NAME + "   " + BuildConfig.VERSION_CODE);
        Log.i("TAG", "On create");

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TextView textImei = findViewById(R.id.imeiTextView);
            textImei.setText(((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        }

        requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        TextView textImei = findViewById(R.id.imeiTextView);
                        textImei.setText(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
                    }
                    catch (SecurityException e) {
                        Log.e("ALARMA", "PERMISSION DENIED");
                    }
                } else {
                    requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
