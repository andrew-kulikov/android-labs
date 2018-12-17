package com.example.kek.labs.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kek.labs.Activity.RssViewActivity;
import com.example.kek.labs.Models.FeedItem;
import com.example.kek.labs.R;
import com.example.kek.labs.Util.GlideApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RssNewsAdapter extends RecyclerView.Adapter<RssNewsAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<FeedItem> rssRecords;
    private Context context;

    public RssNewsAdapter(List<FeedItem> records, Context context) {
        if (context != null)
            inflater = LayoutInflater.from(context);
        rssRecords = records;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = null;
        if (inflater != null)
            card = inflater.inflate(R.layout.rss_card, parent, false);

        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
        holder.descriptionTextView.setText(rssRecords.get(position).getDescription());
        GlideApp.with(context)
                .load(rssRecords.get(position).getThumbnailUrl())
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RssViewActivity.class);
                intent.putExtra("link", rssRecords.get(position).getLink());
                context.startActivity(intent);
            }
        });
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
        TextView descriptionTextView;
        ImageView imageView;
        CardView cardView;

        ViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.card_title_text_view);
            dateTextView = v.findViewById(R.id.card_date_text_view);
            imageView = v.findViewById(R.id.card_image);
            cardView = v.findViewById(R.id.rss_card);
            descriptionTextView = v.findViewById(R.id.card_description_text_view);
        }
    }
}