package com.example.kek.labs.Util;

import android.os.Environment;

import com.example.kek.labs.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {
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
                + MyApplication.getAppContext().getPackageName()
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

    public boolean isFilePresent(String fileName) {
        String path = getDirectoryPath() + File.separator + fileName;
        File file = new File(path);
        return file.exists();
    }
}
