package com.example.kek.labs.Managers;

import android.os.Environment;

import com.example.kek.labs.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileManager {
    private String read(String fileName) {
        try {
            File file = new File(getDirectoryPath() + File.separator + fileName);
            int length = (int) file.length();

            byte[] bytes = new byte[length];
            try (FileInputStream in = new FileInputStream(file)) {
                in.read(bytes);
            }

            return new String(bytes);
        } catch (IOException ioException) {
            return null;
        }
    }

    public static String getDirectoryPath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + MyApplication.getAppContext().getPackageName()
                + "/Files";
    }

    private boolean create(String fileName, String jsonString) {
        try {
            File file = new File(getDirectoryPath(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (IOException ioException) {
            return false;
        }
    }

    private boolean isFilePresent(String fileName) {
        String path = getDirectoryPath() + File.separator + fileName;
        File file = new File(path);
        return file.exists();
    }
}
