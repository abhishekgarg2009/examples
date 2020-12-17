package org.tensorflow.lite.examples.classification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.classification.storage.Basket;
import org.tensorflow.lite.examples.classification.storage.MyItemList;
import org.tensorflow.lite.examples.classification.storage.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private List<MyItemList> listdata;

    // RecyclerView recyclerView;
    public MyListAdapter() {
        this.listdata = new ArrayList<>();
    }

    public void updateList(List<MyItemList> listdata) {
        this.listdata.clear();
        this.listdata.addAll(listdata);

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyItemList myListData = listdata.get(position);

        holder.name.setText(listdata.get(position).getItemName());
        holder.price.setText(String.valueOf(listdata.get(position).getPrice()));
        holder.count.setText(String.valueOf(listdata.get(position).getCount()));
        holder.imageView.setImageResource(listdata.get(position).getImageResource());
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView name;
        public TextView price;
        public TextView count;
        ImageView plusImageView, minusImageView;

        int currentCount;

        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView)  {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.png);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.count = (TextView) itemView.findViewById(R.id.count);
            this.plusImageView = (ImageView) itemView.findViewById(R.id.plus_item);
            this.minusImageView = (ImageView) itemView.findViewById(R.id.minus_item);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.itemLayout);

            this.plusImageView.setOnClickListener(this);
            this.minusImageView.setOnClickListener(this);

            currentCount = Integer.parseInt(count.getText().toString());;
        }

        public void setCurrentCount(int count){
            currentCount = count;
        }
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.plus_item) {
                int itemCount = Integer.parseInt(count.getText().toString());
                if (itemCount >= 50) return;
                setCurrentCount(++itemCount);
                count.setText(String.valueOf(itemCount));

            } else if (v.getId() == R.id.minus_item) {
                int itemCount = Integer.parseInt(count.getText().toString());
                if (itemCount == 0) {
                    return;
                }
                setCurrentCount(--itemCount);
                count.setText(String.valueOf(itemCount));
            }

        }
    }
}