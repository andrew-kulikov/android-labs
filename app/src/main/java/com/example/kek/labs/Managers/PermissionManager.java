package com.example.kek.labs.Managers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import com.example.kek.labs.R;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class PermissionManager {
    private Fragment fragment;

    public PermissionManager(Fragment fragment) {
        this.fragment = fragment;
    }

    public void requestPermissions(String[] permissions, int requestCode) {
        ArrayList<String> toRequest = new ArrayList<>();
        ArrayList<String> toExplain = new ArrayList<>();

        for (String permission : permissions) {
            int hasPermission = fragment.getActivity().checkSelfPermission(permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED)
                if (fragment.shouldShowRequestPermissionRationale(permission))
                    toExplain.add(permission);
                else
                    toRequest.add(permission);
        }
        String[] toExplainArray = new String[toExplain.size()];
        toExplainArray = toExplain.toArray(toExplainArray);

        if (toExplainArray != null && toExplain.size() > 0)
            showPermissionExplanation(toExplainArray, requestCode);

        String[] toRequestArray = new String[toRequest.size()];
        toRequestArray = toRequest.toArray(toRequestArray);

        if (toRequestArray != null && toRequest.size() > 0)
            fragment.requestPermissions(toRequestArray, requestCode);
    }

    private void showPermissionExplanation(final String[] permissions, final int requestCode) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(fragment.getActivity());
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permission_explanation_title);
        alertBuilder.setMessage(R.string.permission_explanation_content);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                fragment.requestPermissions(permissions, requestCode);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
