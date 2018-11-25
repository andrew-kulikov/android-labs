package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.kek.labs.BuildConfig;
import com.example.kek.labs.R;

public class AboutFragment extends Fragment {
    private final int MY_REQUEST_CODE = 1488;
    private View aboutView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        aboutView = inflater.inflate(R.layout.about_fragment, container, false);

        TextView text1 = aboutView.findViewById(R.id.text1);

        text1.setText(BuildConfig.VERSION_NAME + "   " + BuildConfig.VERSION_CODE);

        if (getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                showPermissionExplanation();
            } else
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
        } else
            setImeiText();
        return aboutView;
    }

    private void setImeiText() {
        try {
            TextView textImei = aboutView.findViewById(R.id.imeiTextView);
            textImei.setText(((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        } catch (SecurityException e) {
            Log.e("Error", "PERMISSION DENIED");
        }
    }

    protected void showPermissionExplanation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
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

        switch (requestCode) {
            case MY_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setImeiText();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))
                        showPermissionExplanation();
                    else
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, MY_REQUEST_CODE);
                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
