package com.example.kek.labs.Data;

import android.util.Log;

import com.example.kek.labs.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public final class UserStorage {
    public static User getUser(final String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.child(id).getValue(User.class);
                Log.d("Database", "Value is: " + value.getName());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
    }

    public static void saveUser(final String id, User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.child(id).setValue(user);
    }
}
