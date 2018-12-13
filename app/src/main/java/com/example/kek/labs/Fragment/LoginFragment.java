package com.example.kek.labs.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kek.labs.Activity.MainActivity;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.AuthEventListener;
import com.example.kek.labs.Util.UserManager;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class LoginFragment extends Fragment {

    private View loginView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;
    private NavController navController;
    private UserManager userManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginView = inflater.inflate(R.layout.fragment_login, container, false);

        userManager = new UserManager(getActivity());

        setupNavController();
        setupViews();
        setupButtons();
        setupAuth();

        return loginView;
    }

    private void setupAuth() {
        if (userManager.isUserLogged()) {
            loginSuccess();
        }
    }

    private void loginSuccess() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void setupViews() {
        loginFormView = loginView.findViewById(R.id.login_form);
        progressView = loginView.findViewById(R.id.login_progress);
        emailView = loginView.findViewById(R.id.email);
        passwordView = loginView.findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupButtons() {
        Button mEmailSignInButton = loginView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = loginView.findViewById(R.id.open_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.registerFragment);
            }
        });
    }

    private void setupNavController() {
        NavHostFragment host = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = host.getNavController();
    }

    private void attemptLogin() {
        if (userManager.isLoginProcessing()) {
            return;
        }

        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userManager.login(email, password, new AuthEventListener() {
                @Override
                public void onAuthSuccess() {
                    loginSuccess();
                }

                @Override
                public void onAuthFail() {
                    loginFail();
                }

                @Override
                public void onCancel() {
                    loginCancel();
                }
            });
        }
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

    private void loginFail() {
        showProgress(false);
        Toast.makeText(getActivity(), getString(R.string.auth_fail),
                Toast.LENGTH_SHORT).show();
        passwordView.setError(getString(R.string.error_incorrect_password));
        passwordView.requestFocus();
    }

    private void loginCancel() {
        showProgress(false);
    }
}
