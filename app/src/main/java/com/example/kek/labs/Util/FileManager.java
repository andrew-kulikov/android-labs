package com.example.kek.labs.Util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileManager {
    private Activity activity;

    public FileManager(Activity activity) {
        this.activity = activity;
    }

    public String read(String fileName) {
        try {
            File file = new File(getDirectoryPath() + File.separator + fileName);
            int length = (int) file.length();

            byte[] bytes = new byte[length];
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }

            return new String(bytes);
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    public String getDirectoryPath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/Files";
    }

    public boolean create(String fileName, String jsonString) {
        try {
            File file = new File(getDirectoryPath(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }
/*
    private void write(String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(activity.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }*/

    public boolean isFilePresent(String fileName) {
        String path = getDirectoryPath() + File.separator + fileName;
        File file = new File(path);
        return file.exists();
    }
}
