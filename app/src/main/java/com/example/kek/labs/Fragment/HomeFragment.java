package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kek.labs.Managers.PermissionManager;
import com.example.kek.labs.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    private final int REQUEST_INTERNET = 228;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PermissionManager permissionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.home_fragment, container, false);

        setupUri();
        setupPermissions();

        mRecyclerView = homeView.findViewById(R.id.rss_recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<String> cards = new ArrayList<>();
        cards.add("3244");
        cards.add("syn");
        cards.add("sobaki");
        cards.add("naruto");
        mAdapter = new MyAdapter(cards, getContext());
        mRecyclerView.setAdapter(mAdapter);

        return homeView;
    }

    private void setupPermissions() {
        String[] permissions = {Manifest.permission.INTERNET};

        permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions(permissions, REQUEST_INTERNET);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.requestPermissions(permissions, REQUEST_INTERNET);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        LayoutInflater inflater;
        List<String> rssRecords;
        Context context;
        public MyAdapter(List<String> records, Context context) {
            inflater = LayoutInflater.from(context);
            rssRecords = records;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View card = inflater.inflate(R.layout.rss_card, parent, false);

            return new ViewHolder(card);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(rssRecords.get(position));
        }

        @Override
        public int getItemCount() {
            try {
                return rssRecords.size();
            } catch (Exception e) {
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.card_text_view);
            }
        }
    }

}
