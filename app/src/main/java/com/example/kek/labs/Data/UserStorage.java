package com.example.kek.labs.Data;

import android.util.Log;

import com.example.kek.labs.Models.User;
import com.example.kek.labs.Util.UserSaveListener;
import com.example.kek.labs.Util.UserUpdateListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public final class UserStorage {
    public static void getUser(final String id, final UserUpdateListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(id).getValue(User.class);
                listener.onUpdateUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("FDatabase", "Failed to read value.", error.toException());
            }
        });
    }

    public static void saveUser(final String id, User user, final UserSaveListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        ref.child(id).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.onSaveUserError();
                } else {
                    listener.onSaveUserSuccess();
                }
            }
        });
    }
}
