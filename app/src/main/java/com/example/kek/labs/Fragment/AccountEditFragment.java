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
import android.widget.Toast;

import com.example.kek.labs.Activity.MainActivity;
import com.example.kek.labs.Managers.ImageManager;
import com.example.kek.labs.Managers.PermissionManager;
import com.example.kek.labs.Managers.UserManager;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.SaveImageListener;
import com.example.kek.labs.Util.UserSaveListener;
import com.example.kek.labs.Util.UserUpdateListener;
import com.example.kek.labs.Util.Validator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import static android.app.Activity.RESULT_OK;

public class AccountEditFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final int REQUEST_WRITE = 3;
    private View editView;
    private ImageView logo;
    private PermissionManager permissionManager;
    private ImageManager imageManager;
    private UserManager userManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        editView = inflater.inflate(R.layout.account_edit_fragment, container, false);

        userManager = UserManager.getInstance(getActivity());

        setEditLogoClick();
        setSaveClick();
        setupPermissions();
        setupLogo();
        setupTextViews(savedInstanceState);

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

        SaveImageListener listener = new SaveImageListener() {
            @Override
            public void onImageDownloadFinished() {

            }
        };
        imageManager.LoadAvatar(logo, R.drawable.about, listener);
    }

    private void setupTextViews(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setViewText(R.id.info_email_textEdit, savedInstanceState.getString("email"));
            setViewText(R.id.info_name_textEdit, savedInstanceState.getString("name"));
            setViewText(R.id.info_surname_textEdit, savedInstanceState.getString("surname"));
            setViewText(R.id.info_phone_textEdit, savedInstanceState.getString("phone"));
        } else
            userManager.getUser(new UserUpdateListener() {
                @Override
                public void onUpdateUser(User user) {
                    setText(R.id.info_email_textEdit,
                            user.getEmail());
                    setText(R.id.info_name_textEdit,
                            user.getName());
                    setText(R.id.info_surname_textEdit,
                            user.getSurname());
                    setText(R.id.info_phone_textEdit,
                            user.getPhone());
                }
            });
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
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            Intent intent = imageManager.getPickImageIntent(REQUEST_IMAGE_CAPTURE, REQUEST_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_LOAD_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        SaveImageListener imageListener = new SaveImageListener() {
            @Override
            public void onImageDownloadFinished() {
                Log.d("Images save", "ok");
            }
        };
        switch (requestCode) {
            case REQUEST_LOAD_IMAGE: {
                Uri imageUri = data.getData();

                if (imageUri != null) {
                    logo.setImageURI(imageUri);
                    Bitmap bmp = ((BitmapDrawable) logo.getDrawable()).getBitmap();
                    imageManager.storeImage(bmp, imageListener);
                } else {
                    Bundle extras = data.getExtras();
                    if (extras == null) break;
                    Bitmap takenPhoto = (Bitmap) extras.get("data");
                    logo.setImageBitmap(takenPhoto);
                    imageManager.storeImage(takenPhoto, imageListener);
                }
                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                Bundle extras = data.getExtras();
                if (extras == null) break;
                Bitmap takenPhoto = (Bitmap) data.getExtras().get("data");
                logo.setImageBitmap(takenPhoto);
                imageManager.storeImage(takenPhoto, imageListener);
            }
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.refreshHeader();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("email", getViewText(R.id.info_email_textEdit));
        outState.putString("name", getViewText(R.id.info_name_textEdit));
        outState.putString("surname", getViewText(R.id.info_surname_textEdit));
        outState.putString("phone", getViewText(R.id.info_phone_textEdit));

        super.onSaveInstanceState(outState);
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
                phone);

        userManager.saveUser(user, new UserSaveListener() {
            @Override
            public void onSaveUserSuccess() {
                Toast.makeText(getContext(), "User saved successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaveUserError() {
                Toast.makeText(getContext(), "Error while saving user", Toast.LENGTH_LONG).show();
            }
        });

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.refreshHeader();
    }

    private String getViewText(int viewId) {
        return ((EditText) editView.findViewById(viewId)).getText().toString();
    }

    private void setViewText(int viewId, String text) {
        ((EditText) editView.findViewById(viewId)).setText(text);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.requestPermissions(permissions, REQUEST_WRITE);
    }
}
