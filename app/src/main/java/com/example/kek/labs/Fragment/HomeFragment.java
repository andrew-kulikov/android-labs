package com.example.kek.labs.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kek.labs.Managers.FileManager;
import com.example.kek.labs.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment {
    private View homeView;
    private NavController navController;
    private ListView addressListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_fragment, container, false);

        setupUri();
        setupNavController();
        setupListView();


        homeView.findViewById(R.id.add_address_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> addresses = FileManager.readAddresses();
                EditText addressEdit = homeView.findViewById(R.id.rss_address_text_edit);
                if (addresses == null) addresses = new ArrayList<>();
                addresses.add(addressEdit.getText().toString());
                FileManager.saveAddresses(addresses);
                setupAdapter(addresses);
            }
        });


        return homeView;
    }

    private void setupAdapter(List<String> addresses) {
        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(getContext(), R.layout.address_list_item, addresses);
        addressListView.setAdapter(addressAdapter);
    }

    private void setupListView() {
        addressListView = homeView.findViewById(R.id.news_address_list);

        final List<String> addresses = FileManager.readAddresses();
        if (addresses == null) {
            FileManager.saveAddresses(new ArrayList<String>());
            Toast.makeText(getContext(), "Cannot find news sources", Toast.LENGTH_SHORT).show();
            return;
        }

        setupAdapter(addresses);

        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("address", addresses.get(position));
                navController.navigate(R.id.newsFragment, bundle);
            }
        });
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
