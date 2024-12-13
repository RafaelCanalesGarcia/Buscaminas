package com.example.buscaminas;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> items;
    private List<String> positions;

    public MyAdapter(List<String> items, List<String> positions) {
        this.items = items;
        this.positions = positions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameUser;
        TextView positionUser;

        public ViewHolder(View itemView) {
            super(itemView);
            nameUser = itemView.findViewById(R.id.nameOfFriend);
            positionUser = itemView.findViewById(R.id.positionOfFriend);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_card_view,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.nameUser.setText(items.get(position));
        holder.positionUser.setText(positions.get(position));
    }
    @Override
    public int getItemCount(){
        return items.size();
    }
}

