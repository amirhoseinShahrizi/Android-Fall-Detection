package com.example.fall_detection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FallCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<DetectedFall> detectedFallList;
    public FallCardAdapter(final List<DetectedFall> detectedFallList){
        this.detectedFallList = detectedFallList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_detected_fall, viewGroup, false);
        return new FallCardViewHolder(v);
    }

    //the position argument can be a final
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FallCardViewHolder thisHolder = (FallCardViewHolder) holder;
        thisHolder.card_date.setText(detectedFallList.get(position).getCurrent_date());
        thisHolder.card_time.setText(detectedFallList.get(position).getCurrent_time());
    }

    @Override
    public int getItemCount() {
        return detectedFallList.size();
    }


    public static class FallCardViewHolder extends RecyclerView.ViewHolder {

        TextView card_date, card_time;

        public FallCardViewHolder(View itemView) {
            super(itemView);
            card_date = itemView.findViewById(R.id.fall_card_date);
            card_time = itemView.findViewById(R.id.fall_card_time);
        }
    }
}
