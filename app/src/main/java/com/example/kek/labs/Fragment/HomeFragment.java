package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kek.labs.Adapters.RssNewsAdapter;
import com.example.kek.labs.Managers.PermissionManager;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.Parser;
import com.example.kek.labs.Util.RssReaderTask;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment implements RssReaderTask.onDownloadedListener {
    private final int REQUEST_INTERNET = 228;
    private View homeView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PermissionManager permissionManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_fragment, container, false);

        setupUri();
        setupPermissions();
        setupRefreshLayout();
        setupLayoutManager();
        setupRecyclerView();

        return homeView;
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout = homeView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        RssReaderTask readerTask = new RssReaderTask("https://news.tut.by/rss/index.rss",
                                false).addOnDownloadListener(HomeFragment.this);
                        readerTask.execute();
                    }
                }
        );
    }

    private void setupRecyclerView() {
        mRecyclerView = homeView.findViewById(R.id.rss_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RssReaderTask readerTask = new RssReaderTask("https://news.tut.by/rss/index.rss", true).addOnBeforeDownloadListener(new RssReaderTask.onBeforeDownloadListener() {
            @Override
            public void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }
        }).addOnDownloadListener(this);
        readerTask.execute();
    }

    private void setupLayoutManager() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
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

    @Override
    public void onPostExecute(Document rss) {
        mAdapter = new RssNewsAdapter(Parser.parseRss(rss), getContext());
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
