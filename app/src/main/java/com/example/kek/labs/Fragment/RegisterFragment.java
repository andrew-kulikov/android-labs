package com.example.kek.labs.Fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kek.labs.Activity.MainActivity;
import com.example.kek.labs.Managers.UserManager;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.AuthEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import androidx.fragment.app.Fragment;


public class RegisterFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference myRef;
    private View registerView;
    private UserManager userManager;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();

        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerView = inflater.inflate(R.layout.fragment_register, container, false);

        userManager = new UserManager(getActivity());

        mEmailView = registerView.findViewById(R.id.register_email_edit);
        mPasswordView = registerView.findViewById(R.id.register_password_edit);

        Button signUpButton = registerView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mLoginFormView = registerView.findViewById(R.id.register_form);
        mProgressView = registerView.findViewById(R.id.register_progress);
        mAuth = FirebaseAuth.getInstance();

        return registerView;
    }

    private void register() {
        if (userManager.isRegisterProcessing()) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmation = ((TextView) registerView.findViewById(R.id.register_repeat_password_edit)).getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) ||
                !TextUtils.isEmpty(password) && !isPasswordValid(password) ||
                !password.equals(confirmation)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            User user = getModelFromViews();
            userManager.register(user, password, new AuthEventListener() {
                @Override
                public void onAuthSuccess() {
                    registerSuccess();
                }

                @Override
                public void onAuthFail() {
                    registerFail();
                }

                @Override
                public void onCancel() {
                    registerCancel();
                }
            });
        }
    }

    private User getModelFromViews() {
        String name = getViewText(R.id.register_name_edit);
        String surname = getViewText(R.id.register_surname_edit);
        String phone = getViewText(R.id.register_phone_edit);
        String email = getViewText(R.id.register_email_edit);

        return new User(email, name, surname, phone);
    }

    private String getViewText(int id) {
        return ((TextView)registerView.findViewById(id)).getText().toString();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void registerSuccess() {
        FirebaseUser user = mAuth.getCurrentUser();
        myRef = database.getReference("users");
        myRef.child(user.getUid()).child("name").setValue(((TextView) registerView.findViewById(R.id.register_name_edit)).getText().toString());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void registerFail() {
        showProgress(false);
        Toast.makeText(getActivity(), "Authentication failed.",
                Toast.LENGTH_SHORT).show();
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
    }

    private void registerCancel() {
        showProgress(false);
    }


}
