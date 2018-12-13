package com.example.kek.labs.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.Data.Storage;
import com.example.kek.labs.Models.User;
import com.example.kek.labs.R;
import com.example.kek.labs.Managers.ImageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class AccountInfoFragment extends Fragment {
    private View infoView;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.account_info_fragment, container, false);

        setupLogo();
        setupNavController();
        setEditButtonClick();
        setupTextViews();

        return infoView;
    }

    private void setupLogo() {
        ImageView logo = infoView.findViewById(R.id.accountLogo);
        ImageManager imageManager = new ImageManager();
        imageManager.LoadImage(logo,
                "logo.jpg",
                R.drawable.about);
    }

    private void setupNavController() {
        NavHostFragment host = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
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
        User user = Storage.getApplicationUser();
        if (user == null) return;

        setText(R.id.info_email_textView,
                user.getEmail(),
                getString(R.string.email_default));
        setText(R.id.info_name_textView,
                user.getName(),
                getString(R.string.name_default));
        setText(R.id.info_surname_textView,
                user.getSurname(),
                getString(R.string.surname_default));
        setText(R.id.info_phone_textView,
                user.getPhone(),
                getString(R.string.phone_default));
    }

    private void setText(int viewId, String value, String defaultValue) {
        String finalValue = value != null ? value : defaultValue;
        ((TextView) infoView.findViewById(viewId)).setText(finalValue);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
