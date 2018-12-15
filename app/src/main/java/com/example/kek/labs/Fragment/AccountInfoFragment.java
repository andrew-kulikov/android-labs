package com.example.kek.labs.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.Managers.ImageManager;
import com.example.kek.labs.Managers.UserManager;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.DownloadImageListener;
import com.example.kek.labs.Util.UserUpdateListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class AccountInfoFragment extends Fragment {
    private View infoView;
    private NavController navController;
    private UserManager userManager;
    private View infoFormView;
    private View progressView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.account_info_fragment, container, false);

        userManager = UserManager.getInstance(getActivity());

        setupViews();
        setupLogo();
        setupNavController();
        setEditButtonClick();
        setupTextViews();

        return infoView;
    }

    private void setupViews() {
        infoFormView = infoView.findViewById(R.id.account_info_form);
        progressView = infoView.findViewById(R.id.image_progress);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        infoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        infoFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                infoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    private void setupLogo() {
        showProgress(true);
        ImageView logo = infoView.findViewById(R.id.accountLogo);
        ImageManager imageManager = new ImageManager();

        DownloadImageListener listener = new DownloadImageListener() {
            @Override
            public void onImageDownloadFinished() {
                showProgress(false);
            }
        };
        imageManager.LoadAvatar(logo, R.drawable.about, listener);
    }

    private void setupNavController() {
        FragmentActivity activity = getActivity();
        if (activity == null) return;

        NavHostFragment host = (NavHostFragment) activity
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (host != null)
            navController = host.getNavController();
    }

    private void setEditButtonClick() {
        infoView.findViewById(R.id.edit_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.accountEditFragment);
            }
        });
    }

    private void setupTextViews() {
        userManager.getUser(new UserUpdateListener() {
            @Override
            public void onUpdateUser(User user) {
                setText(R.id.info_email_textView,
                        user.getEmail());
                setText(R.id.info_name_textView,
                        user.getName());
                setText(R.id.info_surname_textView,
                        user.getSurname());
                setText(R.id.info_phone_textView,
                        user.getPhone());
            }
        });
    }

    private void setText(int viewId, String value) {
        ((TextView) infoView.findViewById(viewId)).setText(value);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
