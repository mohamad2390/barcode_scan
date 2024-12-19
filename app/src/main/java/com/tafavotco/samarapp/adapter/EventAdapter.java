package com.tafavotco.samarapp.adapter;

import static com.tafavotco.samarapp.data.constValue.server_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tafavotco.samarapp.MainActivity;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.model.ActivityModel;
import com.tafavotco.samarapp.model.EventModel;
import com.tafavotco.samarapp.ui.Verifying;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<EventModel> data;
    private LayoutInflater mInflater;
    Context context;

    public EventAdapter(List<EventModel> data, Context context) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =mInflater.inflate(R.layout.item_list_event, parent , false);
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.item_txt_event_title.setText(data.get(position).getTitle());
        holder.item_txt_event_description.setText(data.get(position).getDescription());
        holder.item_txt_event_time.setText(data.get(position).getStartDate().toString());

        Glide.with(context)
                .load(server_URL+"events/cover/"+data.get(position).getId())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.item_image_event_cover);

        holder.item_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                preferencesHelper.setEvent(data.get(position).getId());
                Intent myIntent = new Intent(context , MainActivity.class);
                context.startActivity(myIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout item_event;
        public ImageView item_image_event_cover;
        public TextView item_txt_event_title;
        public TextView item_txt_event_description;
        public TextView item_txt_event_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_event =itemView.findViewById(R.id.item_event);
            item_image_event_cover = itemView.findViewById(R.id.image_event_cover);
            item_txt_event_title = itemView.findViewById(R.id.txt_event_title);
            item_txt_event_description = itemView.findViewById(R.id.txt_event_description);
            item_txt_event_time = itemView.findViewById(R.id.txt_event_time);
        }
    }
}
