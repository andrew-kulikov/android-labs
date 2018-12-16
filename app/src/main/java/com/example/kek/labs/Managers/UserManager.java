package com.example.kek.labs.Managers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kek.labs.Data.UserStorage;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.Listeners.AuthEventListener;
import com.example.kek.labs.Listeners.UserSaveListener;
import com.example.kek.labs.Listeners.UserUpdateListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class UserManager {
    @SuppressLint("StaticFieldLeak")
    private static final UserManager instance = new UserManager();

    private Activity activity;
    private UserLoginTask loginTask;
    private UserRegisterTask registerTask;
    private User user;

    private UserManager() {
    }

    public static UserManager getInstance(Activity activity) {
        instance.activity = activity;
        return instance;
    }

    public boolean isUserLogged() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return user != null && !user.isAnonymous();
    }

    public void getUser(final UserUpdateListener listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null || firebaseUser.getEmail() == null) return;

        if (user == null || !firebaseUser.getEmail().equals(user.getEmail())) {
            UserStorage.getUser(FirebaseAuth.getInstance().getUid(), new UserUpdateListener() {
                @Override
                public void onUpdateUser(User _user) {
                    user = _user;
                    listener.onUpdateUser(user);
                }
            });
        } else listener.onUpdateUser(user);
    }

    public void saveUser(User user, final UserSaveListener listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        firebaseUser.updateEmail(user.getEmail());
        UserStorage.saveUser(firebaseUser.getUid(), user, listener);
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

    @SuppressLint("StaticFieldLeak")
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

                            loginTask = null;
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

    @SuppressLint("StaticFieldLeak")
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
                                if (curUser == null) return;

                                UserStorage.saveUser(curUser.getUid(), user, new UserSaveListener() {
                                    @Override
                                    public void onSaveUserSuccess() {
                                        Log.d("Registration", "User saved successfully");
                                    }

                                    @Override
                                    public void onSaveUserError() {
                                        Log.d("Registration", "Db error while saving");
                                    }
                                });
                                listener.onAuthSuccess();
                            } else
                                listener.onAuthFail();
                            registerTask = null;
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
