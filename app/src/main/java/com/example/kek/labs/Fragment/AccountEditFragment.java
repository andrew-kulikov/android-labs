package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kek.labs.R;
import com.example.kek.labs.Util.FileManager;
import com.example.kek.labs.Util.ImageManager;
import com.example.kek.labs.Util.PermissionManager;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class AccountEditFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final int REQUEST_WRITE = 3;
    private View editView;
    private ImageView logo;
    private PermissionManager permissionManager;
    private ImageManager imageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        editView = inflater.inflate(R.layout.account_edit_fragment, container, false);

        logo = editView.findViewById(R.id.accountLogo);

        logo = editView.findViewById(R.id.accountLogo);
        editView.findViewById(R.id.edit_logo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        editView.findViewById(R.id.save_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

        permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions(permissions, REQUEST_WRITE);

        imageManager = new ImageManager(this.getActivity());
        imageManager.LoadImage(
                logo,
                "logo.jpg",
                R.drawable.about);

        FileManager fileManager = new FileManager(getActivity());
        String data = fileManager.read("storage.json");
        try {
            JSONObject json = new JSONObject(data);
            setText(R.id.info_email_textEdit, json.getString("email"));
            setText(R.id.info_name_textEdit, json.getString("name"));
            setText(R.id.info_surname_textEdit, json.getString("surname"));
            setText(R.id.info_phone_textEdit, json.getString("phone"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return editView;
    }

    private void setText(int viewId, String value) {
        ((EditText) editView.findViewById(viewId)).setText(value);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            Intent intent = imageManager.getPickImageIntent(REQUEST_IMAGE_CAPTURE, REQUEST_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_LOAD_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOAD_IMAGE: {
                if (resultCode != RESULT_OK)
                    return;

                Uri imageUri = data.getData();

                if (imageUri != null) {
                    logo.setImageURI(imageUri);

                    Bitmap bmp = ((BitmapDrawable) logo.getDrawable()).getBitmap();

                    imageManager.storeImage(bmp);
                } else {
                    Bitmap takenPhoto = (Bitmap) data.getExtras().get("data");

                    logo.setImageBitmap(takenPhoto);
                    imageManager.storeImage(takenPhoto);
                }

                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                Bitmap takenPhoto = (Bitmap) data.getExtras().get("data");

                logo.setImageBitmap(takenPhoto);
                imageManager.storeImage(takenPhoto);
            }
        }
    }

    private void onSaveButtonClick() {
        FileManager fileManager = new FileManager(getActivity());

        String userJson = constructJson();
        Log.i("Save", userJson);
        boolean isFileCreated = fileManager.create("storage.json", userJson);
        if (isFileCreated) {
            Log.i("Save", "Ok");
        } else {
            //show error or try again.
        }

    }

    private String constructJson() {
        String email = ((EditText) editView.findViewById(R.id.info_email_textEdit)).getText().toString();
        String name = ((EditText) editView.findViewById(R.id.info_name_textEdit)).getText().toString();
        String surname = ((EditText) editView.findViewById(R.id.info_surname_textEdit)).getText().toString();
        String phone = ((EditText) editView.findViewById(R.id.info_phone_textEdit)).getText().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("name", name);
            json.put("surname", surname);
            json.put("phone", phone);

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.requestPermissions(permissions, REQUEST_WRITE);
    }
}
