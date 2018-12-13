package com.example.kek.labs.Managers;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.kek.labs.Data.UserStorage;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.Util.AuthEventListener;
import com.example.kek.labs.Util.UserUpdateListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class UserManager {
    private static final UserManager instance = new UserManager();

    private Activity activity;
    private UserLoginTask loginTask;
    private UserRegisterTask registerTask;
    private User user;

    private UserManager() {}

    public static UserManager getInstance(Activity activity) {
        instance.activity = activity;
        return instance;
    }

    public boolean isUserLogged() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return user != null && !user.isAnonymous();
    }

    public void getUser(final UserUpdateListener listener) {

        if (user == null) {
            UserStorage.getUser(FirebaseAuth.getInstance().getUid(), new UserUpdateListener() {
                @Override
                public void UpdateUser(User _user) {
                    user = _user;
                    listener.UpdateUser(user);
                }
            });
        } else listener.UpdateUser(user);
    }

    public void register(User user, String password, AuthEventListener listener) {
        registerTask = new UserRegisterTask(user, password, listener);
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

        private final User user;
        private final String password;
        private AuthEventListener listener;

        UserRegisterTask(User user, String password, AuthEventListener listener) {
            this.user = user;
            this.password = password;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
                                UserStorage.saveUser(curUser.getUid(), user);
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
