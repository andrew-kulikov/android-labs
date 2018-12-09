package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.GlideApp;
import com.example.kek.labs.Util.PermissionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class AccountInfoFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final int REQUEST_WRITE = 3;
    private View infoView;
    private ImageView logo;
    private PermissionManager permissionManager;

    private static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra("requestCode", REQUEST_LOAD_IMAGE);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra("requestCode", REQUEST_IMAGE_CAPTURE);
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.edit_account));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.account_info_fragment, container, false);

        logo = infoView.findViewById(R.id.accountLogo);
        infoView.findViewById(R.id.selectLogoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

        permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions(permissions, REQUEST_WRITE);

        GlideApp.with(getContext())
                .load(getLogoDirectoryPath() + "/logo.jpg")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.about)
                .into(logo);

        return infoView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            Intent intent = getPickImageIntent(getActivity());
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

                    storeImage(bmp);
                } else {
                    Bitmap takenPhoto = (Bitmap) data.getExtras().get("data");

                    logo.setImageBitmap(takenPhoto);
                    storeImage(takenPhoto);
                }

                break;
            }
        }
    }

    private String getLogoDirectoryPath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getActivity().getApplicationContext().getPackageName()
                + "/Files";
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getLogoDirectoryPath());

        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdirs())
                return null;

        File mediaFile;
        String mImageName = "logo.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile, false);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Sdf", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("sdfsd", "Error accessing file: " + e.getMessage());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.requestPermissions(permissions, REQUEST_WRITE);
    }
}
