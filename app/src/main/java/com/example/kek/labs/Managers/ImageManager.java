package com.example.kek.labs.Managers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kek.labs.MyApplication;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ImageManager {
    private Context context;
    private StorageReference storageRef;

    public ImageManager() {
        this.context = MyApplication.getAppContext();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public Intent getPickImageIntent(int requestCameraCode, int requestLoadCode) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra("requestCode", requestLoadCode);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra("requestCode", requestCameraCode);
        intentList = addIntentsToList(intentList, pickIntent);
        intentList = addIntentsToList(intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.edit_account));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private List<Intent> addIntentsToList(List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    private String getFilesDirectoryPath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getApplicationContext().getPackageName()
                + "/Files";
    }

    public void LoadImage(ImageView to, String path, int alternative) {
        path = getFilesDirectoryPath() + File.separator + path;

        if (new File(path).exists()) {
            GlideApp.with(context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(alternative)
                    .into(to);
        } else loadFromStorage(alternative, to);
    }

    public void LoadAvatar(ImageView logo, int about) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String path = getAvatarPath();
        LoadImage(logo, path, about);
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getFilesDirectoryPath());

        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdirs())
                return null;

        return new File(getAvatarPath());
    }

    public void storeImage(Bitmap image) {
        saveToStorage();
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile, false);
            image.compress(Bitmap.CompressFormat.PNG, 80, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Image log", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Image log", "Error accessing file: " + e.getMessage());
        }
    }

    private String getAvatarPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return "";

        return getFilesDirectoryPath() + File.separator + user.getUid() + ".jpg";
    }

    private void saveToStorage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        Uri file = Uri.fromFile(new File(getAvatarPath()));
        StorageReference riversRef = storageRef.child("users/" + user.getUid() + "/images/avatar.jpg");

        riversRef.putFile(file);
    }

    private void loadFromStorage(final int alternative, final ImageView to) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        final String path = getAvatarPath();
        File localFile = new File(path);

        StorageReference avatarRef = storageRef.child("users/" + user.getUid() + "/images/avatar.jpg");
        avatarRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        GlideApp.with(context)
                                .load(path)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(alternative)
                                .into(to);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                GlideApp.with(context)
                        .load(alternative)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(to);
            }
        });
    }


}
