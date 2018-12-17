package com.example.kek.labs.Managers;

import android.os.Environment;

import com.example.kek.labs.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FileManager {
    public static List<String> readAddresses() {
        try {
            File file = new File(getDirectoryPath(), "addresses.json");
            int length = (int) file.length();

            byte[] bytes = new byte[length];
            try (FileInputStream in = new FileInputStream(file)) {
                in.read(bytes);
            }
            JSONArray jsonArray = new JSONArray(new String(bytes));
            List<String> list = new ArrayList<>();
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.get(i).toString());
            }
            return list;
        } catch (IOException ioException) {
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDirectoryPath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + MyApplication.getAppContext().getPackageName()
                + "/Files";
    }

    public static boolean saveAddresses(List<String> addresses) {
        try {
            File file = new File(getDirectoryPath(), "addresses.json");
            FileOutputStream fos = new FileOutputStream(file);

            JSONArray array = new JSONArray(addresses);
            String jsonString = array.toString();

            fos.write(jsonString.getBytes());
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
