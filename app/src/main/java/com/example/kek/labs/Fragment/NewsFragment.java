package com.example.kek.labs.Fragment;


import android.Manifest;
import android.content.res.Configuration;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewsFragment extends androidx.fragment.app.Fragment implements RssReaderTask.onDownloadedListener {
    private final int REQUEST_INTERNET = 228;
    private View newsView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PermissionManager permissionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String address;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        newsView = inflater.inflate(R.layout.fragment_news, container, false);

        setupAddress(getArguments());
        setupPermissions();
        setupRefreshLayout();
        setupLayoutManager();
        setupRecyclerView();

        return newsView;
    }

    private void setupAddress(Bundle bundle) {
        if (bundle != null) {
            address = bundle.getString("address");
        }
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout = newsView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        RssReaderTask readerTask = new RssReaderTask(address,
                                false).addOnDownloadListener(NewsFragment.this);
                        readerTask.execute();
                    }
                }
        );
    }

    private void setupRecyclerView() {
        mRecyclerView = newsView.findViewById(R.id.rss_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RssReaderTask readerTask = new RssReaderTask(address, true)
                .addOnBeforeDownloadListener(new RssReaderTask.onBeforeDownloadListener() {
                    @Override
                    public void onPreExecute() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                })
                .addOnDownloadListener(this);
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
        RecyclerView.Adapter mAdapter = new RssNewsAdapter(Parser.parseRss(rss), getContext());
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}

