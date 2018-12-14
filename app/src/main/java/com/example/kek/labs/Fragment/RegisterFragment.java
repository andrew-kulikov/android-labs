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
import com.example.kek.labs.Util.Validator;

import androidx.fragment.app.Fragment;


public class RegisterFragment extends Fragment {
    private View registerView;
    private UserManager userManager;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private View progressView;
    private View loginFormView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerView = inflater.inflate(R.layout.fragment_register, container, false);

        userManager = UserManager.getInstance(getActivity());

        emailView = registerView.findViewById(R.id.register_email_edit);
        passwordView = registerView.findViewById(R.id.register_password_edit);
        confirmPasswordView = registerView.findViewById(R.id.register_repeat_password_edit);

        Button signUpButton = registerView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        loginFormView = registerView.findViewById(R.id.register_form);
        progressView = registerView.findViewById(R.id.register_progress);

        return registerView;
    }

    private void register() {
        if (userManager.isRegisterProcessing()) {
            return;
        }

        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmation = ((TextView) registerView.findViewById(R.id.register_repeat_password_edit)).getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) ||
                !TextUtils.isEmpty(password) && !Validator.isValidPassword(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmation) ||
                !TextUtils.isEmpty(confirmation) && !Validator.isValidPassword(confirmation) ||
                !password.equals(confirmation)) {
            confirmPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!Validator.isValidEmail(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
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

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void registerSuccess() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void registerFail() {
        showProgress(false);
        Toast.makeText(getActivity(), "Authentication failed.",
                Toast.LENGTH_SHORT).show();
        passwordView.setError(getString(R.string.error_incorrect_password));
        passwordView.requestFocus();
    }

    private void registerCancel() {
        showProgress(false);
    }


}
