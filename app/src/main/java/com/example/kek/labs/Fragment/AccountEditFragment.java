package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kek.labs.R;
import com.example.kek.labs.Util.ImageManager;
import com.example.kek.labs.Util.PermissionManager;

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
        editView.findViewById(R.id.save_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

        permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions(permissions, REQUEST_WRITE);

        imageManager = new ImageManager(this.getActivity());
        imageManager.LoadImage(
                logo,
                imageManager.getLogoDirectoryPath() + "/logo.jpg",
                R.drawable.about);

        return editView;
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.requestPermissions(permissions, REQUEST_WRITE);
    }
}
