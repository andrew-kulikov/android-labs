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

import com.example.kek.labs.BuildConfig;
import com.example.kek.labs.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {
    private final int REQUEST_PHONE_STATE_CODE = 1488;
    private View aboutView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        aboutView = inflater.inflate(R.layout.about_fragment, container, false);

        TextView text1 = aboutView.findViewById(R.id.text1);

        String versionCode = getString(R.string.build_info, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        text1.setText(versionCode);

        if (getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_PHONE_STATE)) {
                showPermissionExplanation();
            } else
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_CODE);
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

    private void showPermissionExplanation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permission_explanation_title);
        alertBuilder.setMessage(R.string.permission_explanation_content);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_CODE);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PHONE_STATE_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setImeiText();
                } else {

                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))
                        showPermissionExplanation();

                    else
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_CODE);
                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
