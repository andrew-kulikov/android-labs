package com.example.kek.labs.Util;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.kek.labs.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class UserManager {
    private Activity activity;
    private UserLoginTask loginTask;
    private UserRegisterTask registerTask;
    private User user;

    public UserManager(Activity activity) {
        this.activity = activity;
    }

    public boolean isUserLogged() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return user != null && !user.isAnonymous();
    }

    public User getUser() {
        if (user != null) return user;
        return null;
    }

    public void register(String email, String password, AuthEventListener listener) {
        registerTask = new UserRegisterTask(email, password, listener);
        registerTask.execute((Void) null);
    }

    public boolean isRegisterProcessing() {
        return registerTask != null;
    }

    public boolean isLoginProcessing() {
        return loginTask != null;
    }

    public void login(String email, String password, AuthEventListener listener) {
        loginTask = new UserLoginTask(email, password, listener);
        loginTask.execute((Void) null);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;
        private AuthEventListener listener;

        UserLoginTask(String email, String password, AuthEventListener listener) {
            this.email = email;
            this.password = password;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                                listener.onAuthSuccess();
                            else
                                listener.onAuthFail();
                        }
                    });

            return true;
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            listener.onCancel();
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;
        private AuthEventListener listener;

        UserRegisterTask(String email, String password, AuthEventListener listener) {
            this.email = email;
            this.password = password;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // create db fields
                                listener.onAuthSuccess();
                            } else
                                listener.onAuthFail();
                        }
                    });

            return true;
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            listener.onCancel();
        }
    }
}
