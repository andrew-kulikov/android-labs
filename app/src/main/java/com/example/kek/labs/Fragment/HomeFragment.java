package com.example.kek.labs.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kek.labs.Managers.PermissionManager;
import com.example.kek.labs.Models.FeedItem;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.GlideApp;
import com.example.kek.labs.Util.RssReaderTask;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

        RssReaderTask readerTask = new RssReaderTask("https://news.tut.by/rss/index.rss").addOnDownloadListener(new RssReaderTask.onDownloadedListener() {
            @Override
            public void onPostExecute(Document rss) {
                mAdapter = new MyAdapter(parseRss(rss), getContext());
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        readerTask.execute();

        return homeView;
    }

    private List<FeedItem> parseRss(Document data) {
        List<FeedItem> feedItems = new ArrayList<>();

        if (data == null) return feedItems;

        Element root = data.getDocumentElement();
        Node channel = root.getChildNodes().item(1);
        NodeList items = channel.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Node curChild = items.item(i);
            if (curChild.getNodeName().equalsIgnoreCase("item")) {
                FeedItem item = new FeedItem();
                NodeList childNodes = curChild.getChildNodes();
                String descriptionUri = null;
                String enclosureUri = null;
                String mediaUri = null;
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node curNode = childNodes.item(j);

                    if (curNode.getNodeName().equalsIgnoreCase("title")) {
                        item.setTitle(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("description")) {
                        Elements images = Jsoup.parse(curNode.getTextContent()).getElementsByTag("img");
                        if (images != null && images.size() != 0)
                            descriptionUri = images.get(0).attr("src");
                        item.setDescription(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("pubDate")) {
                        item.setPubDate(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("link")) {
                        item.setLink(curNode.getTextContent());
                    } else if (curNode.getNodeName().equalsIgnoreCase("enclosure")) {
                        enclosureUri = curNode.getAttributes().getNamedItem("url").getTextContent();
                    } else if (curNode.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                        //this will return us thumbnail url
                        mediaUri = curNode.getAttributes().item(0).getTextContent();
                    }
                }
                String url = mediaUri;
                if (mediaUri == null || mediaUri.length() == 0) {
                    if (enclosureUri != null && enclosureUri.length() != 0)
                        url = enclosureUri;
                    else
                        url = descriptionUri;
                }
                item.setThumbnailUrl(url);
                feedItems.add(item);
            }
        }
        return feedItems;
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
        List<FeedItem> rssRecords;
        Context context;

        public MyAdapter(List<FeedItem> records, Context context) {
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
            holder.textView.setText(rssRecords.get(position).getTitle());
            GlideApp.with(context)
                    .load(rssRecords.get(position).getThumbnailUrl())
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            try {
                return rssRecords.size();
            } catch (Exception e) {
                return 0;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView imageView;

            ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.card_text_view);
                imageView = v.findViewById(R.id.card_image);
            }
        }
    }

}
