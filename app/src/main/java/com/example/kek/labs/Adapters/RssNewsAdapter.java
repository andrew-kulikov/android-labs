package com.example.kek.labs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.Models.FeedItem;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.GlideApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RssNewsAdapter extends RecyclerView.Adapter<RssNewsAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<FeedItem> rssRecords;
    private Context context;

    public RssNewsAdapter(List<FeedItem> records, Context context) {
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
        SimpleDateFormat baseFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US);
        SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String date = rssRecords.get(position).getPubDate();
        try {
            date = endFormat.format(baseFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.titleTextView.setText(rssRecords.get(position).getTitle());
        holder.dateTextView.setText(date);
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
        TextView titleTextView;
        TextView dateTextView;
        ImageView imageView;

        ViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.card_title_text_view);
            dateTextView = v.findViewById(R.id.card_date_text_view);
            imageView = v.findViewById(R.id.card_image);
        }
    }
}