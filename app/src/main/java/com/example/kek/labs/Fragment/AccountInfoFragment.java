package com.example.kek.labs.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kek.labs.R;
import com.example.kek.labs.Util.ImageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class AccountInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View infoView = inflater.inflate(R.layout.account_info_fragment, container, false);

        ImageView logo = infoView.findViewById(R.id.accountLogo);
        ImageManager imageManager = new ImageManager(getActivity());

        imageManager.LoadImage(logo,
                imageManager.getLogoDirectoryPath() + "/logo.jpg",
                R.drawable.about);

        NavHostFragment host = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        final NavController controller = host.getNavController();

        infoView.findViewById(R.id.selectLogoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.accountEditFragment);
            }
        });

        return infoView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
