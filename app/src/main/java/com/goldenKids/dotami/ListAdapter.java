package com.goldenKids.dotami;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private ArrayList<ListItem> listItems;
    private Context context;
    public ListAdapter(ArrayList<ListItem> listtItems,Context context) {
        this.listItems =listtItems;
        this.context = context;

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Date,Location,LatLong;
        public ImageView imageView_inf;
        public LinearLayout btn_list;

        public ViewHolder(View itemView) {
            super(itemView);
            Date = itemView.findViewById(R.id.text_Date);
            Location = itemView.findViewById(R.id.text_location);
            LatLong = itemView.findViewById(R.id.text_latlongitude);
            btn_list = itemView.findViewById(R.id.btn_inf);
            imageView_inf = itemView.findViewById(R.id.imageView_search);

        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem item = listItems.get(position);
        holder.imageView_inf.setImageResource(R.drawable.dotami_image1);
        holder.Location.setText(item.getLocation());
        holder.LatLong.setText(item.getLatlong());
        holder.Date.setText(item.getDate());
        holder.btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PotholeInfoActivity.class);

                intent.putExtra("key", item.getUrl());


                context.startActivity(intent);

            }
        });
    }
    public int getItemCount() {
        if (listItems != null) {
            return listItems.size();
        } else {
            return 0; // 또는 다른 기본값을 반환
        }
    }
    public void setListItems(ArrayList<ListItem> listItems){
        this.listItems = listItems;
        notifyDataSetChanged();
    }


}
