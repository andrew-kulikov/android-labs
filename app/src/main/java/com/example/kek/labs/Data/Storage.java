package com.example.kek.labs.Data;

import com.example.kek.labs.Models.User;
import com.example.kek.labs.Util.FileManager;

public final class Storage {
    private static User applicationUser;

    public static User getApplicationUser() {
        return applicationUser;
    }

    public static void setApplicationUser(User applicationUser) {
        Storage.applicationUser = applicationUser;
    }

    public static User getApplicationUser(String fileName) {
        return applicationUser = new FileManager().getUser(fileName);
    }

    public static void setApplicationUser(User applicationUser, String fileName) {
        Storage.applicationUser = applicationUser;
        new FileManager().saveUser(applicationUser, fileName);
    }
}
