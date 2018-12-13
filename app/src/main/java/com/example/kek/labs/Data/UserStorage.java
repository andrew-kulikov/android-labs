package com.example.kek.labs.Data;

import android.util.Log;

import com.example.kek.labs.Models.User;
import com.example.kek.labs.Util.UserUpdateListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public final class UserStorage {
    public static void getUser(final String id, final UserUpdateListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(id).getValue(User.class);
                listener.UpdateUser(user);
                Log.d("FDatabase", "Value is: " + user.getName());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("FDatabase", "Failed to read value.", error.toException());
            }
        });
    }

    public static void saveUser(final String id, User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.child(id).setValue(user);
    }
}
