package com.example.kek.labs.Data;

import com.example.kek.labs.Models.User;

public final class Storage {
    private static User applicationUser;

    public static User getApplicationUser() {
        return applicationUser;
    }

    public static void setApplicationUser(User applicationUser) {
        Storage.applicationUser = applicationUser;
    }
}
