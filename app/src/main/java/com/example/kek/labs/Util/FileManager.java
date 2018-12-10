package com.example.kek.labs.Util;

import android.os.Environment;

import com.example.kek.labs.Models.User;
import com.example.kek.labs.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

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

    private String getDirectoryPath() {
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

    public User getUser(String fileName) {
        if (!isFilePresent("storage.json")) return null;

        User user = null;
        String data = read("storage.json");
        try {
            JSONObject json = new JSONObject(data);

            String email = json.getString("email");
            String name = json.getString("name");
            String surname = json.getString("surname");
            String phone = json.getString("phone");

            user = new User(email, name, surname, phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean saveUser(User user, String fileName) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", user.getEmail());
            json.put("name", user.getName());
            json.put("surname", user.getSurname());
            json.put("phone", user.getPhone());

            return create(fileName, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
