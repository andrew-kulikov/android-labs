package com.example.kek.labs.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kek.labs.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment {
    private View homeView;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_fragment, container, false);

        setupUri();
        setupNavController();

        navController.navigate(R.id.newsFragment);

        return homeView;
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

    private void setupUri() {
        FragmentActivity activity = getActivity();
        if (activity == null) return;

        NavHostFragment host = (NavHostFragment) activity
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (host == null) return;
        NavController controller = host.getNavController();

        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if (action == null) return;

        if (data != null && action.equals("android.intent.action.VIEW")) {
            String path = data.getPath();
            if (path == null) return;

            switch (path) {
                case "/info":
                    controller.navigate(R.id.accountInfoFragment);
                    break;
                case "/about":
                    controller.navigate(R.id.aboutFragment);
                    break;
                default:
                    break;
            }
        }
    }
}
