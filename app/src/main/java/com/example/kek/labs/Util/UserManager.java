package com.example.kek.labs.Util;

import android.app.Activity;
import android.media.MediaDrm;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.kek.labs.Activity.LoginActivity;
import com.example.kek.labs.MyApplication;
import com.example.kek.labs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.EventListener;

import androidx.annotation.NonNull;

public class UserManager {
    private Activity activity;
    private UserLoginTask loginTask;

    public UserManager(Activity activity) {
        this.activity = activity;
    }

    public boolean isUserLogged() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return user != null && !user.isAnonymous();
    }

    public void registerUser() {

    }

    public boolean isProcessing() {
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
                            if (task.isSuccessful()) {
                                //loginSuccess();
                                listener.onAuthSuccess();
                            } else {
                                listener.onAuthFail();
                            }

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
}
