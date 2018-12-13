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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kek.labs.Activity.MainActivity;
import com.example.kek.labs.Data.Storage;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.ImageManager;
import com.example.kek.labs.Util.PermissionManager;
import com.example.kek.labs.Util.Validator;

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

        setEditLogoClick();
        setSaveClick();
        setupPermissions();
        setupLogo();
        setupTextViews();

        return editView;
    }

    private void setSaveClick() {
        editView.findViewById(R.id.save_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });
    }

    private void setEditLogoClick() {
        editView.findViewById(R.id.edit_logo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void setupPermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

        permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions(permissions, REQUEST_WRITE);
    }

    private void setupLogo() {
        logo = editView.findViewById(R.id.accountLogo);
        imageManager = new ImageManager();
        imageManager.LoadImage(
                logo,
                "logo.jpg",
                R.drawable.about);
    }

    private void setupTextViews() {
        User user = Storage.getApplicationUser();
        if (user == null) return;

        setText(R.id.info_email_textEdit,
                user.getEmail(),
                getString(R.string.email_default));
        setText(R.id.info_name_textEdit,
                user.getName(),
                getString(R.string.name_default));
        setText(R.id.info_surname_textEdit,
                user.getSurname(),
                getString(R.string.surname_default));
        setText(R.id.info_phone_textEdit,
                user.getPhone(),
                getString(R.string.phone_default));
    }

    private void setText(int viewId, String value, String defaultValue) {
        String finalValue = value != null ? value : defaultValue;
        ((EditText) editView.findViewById(viewId)).setText(finalValue);
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
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_LOAD_IMAGE: {
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
        ((MainActivity) getActivity()).refreshHeader();
    }

    private void onSaveButtonClick() {
        boolean hasErrors = false;
        String email = getViewText(R.id.info_email_textEdit);
        if (!Validator.isValidEmail(email)) {
            ((EditText) editView.findViewById(R.id.info_email_textEdit)).setError("Invalid email");
            hasErrors = true;
        }

        String phone = getViewText(R.id.info_phone_textEdit);
        if (!Validator.isValidPhone(phone)) {
            ((EditText) editView.findViewById(R.id.info_phone_textEdit)).setError("Invalid phone");
            hasErrors = true;
        }

        if (hasErrors) return;

        User user = new User(
                email,
                getViewText(R.id.info_name_textEdit),
                getViewText(R.id.info_surname_textEdit),
                phone,
                "1");

        try {
            Storage.setApplicationUser(user, "storage.json");
            Toast.makeText(getContext(), "User saved successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getContext(), "Error while saving user", Toast.LENGTH_LONG).show();
        }
        ((MainActivity) getActivity()).refreshHeader();
    }

    private String getViewText(int viewId) {
        return ((EditText) editView.findViewById(viewId)).getText().toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.requestPermissions(permissions, REQUEST_WRITE);
    }
}
