package org.tensorflow.lite.examples.classification;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.classification.storage.Basket;
import org.tensorflow.lite.examples.classification.storage.ItemDetails;
import org.tensorflow.lite.examples.classification.storage.MyItemList;
import org.tensorflow.lite.examples.classification.storage.SharedPreferenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private List<MyItemList> listdata;
    private Map<String, ItemDetails> itemDetailsNameMap;
    private TextView basketView;
    private int i = 0;

    // RecyclerView recyclerView;
    public MyListAdapter(List<MyItemList> listdata, TextView basketView) {
        this.basketView = basketView;
        this.listdata = listdata;
        itemDetailsNameMap = new HashMap<>();
        for(MyItemList myItemList : listdata){
            itemDetailsNameMap.put(myItemList.getItemName(), myItemList.getItemDetails());
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem, itemDetailsNameMap, basketView, ++i);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
        Map<String, ItemDetails> itemDetailsNameMap;
        ImageView plusImageView, minusImageView;

        int currentCount;

        TextView basketView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView, Map<String, ItemDetails> itemDetailsNameMap, TextView basketView, int i)  {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.png);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.count = (TextView) itemView.findViewById(R.id.count);
            this.plusImageView = (ImageView) itemView.findViewById(R.id.plus_item);
            this.minusImageView = (ImageView) itemView.findViewById(R.id.minus_item);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.itemLayout);

            if(i % 2 == 0)
                relativeLayout.setBackgroundColor(Color.parseColor("#E2E2E2"));

            this.plusImageView.setOnClickListener(this);
            this.minusImageView.setOnClickListener(this);

            this.itemDetailsNameMap = itemDetailsNameMap;

            currentCount = Integer.parseInt(count.getText().toString());
            this.basketView = basketView;
        }

        public void setCurrentCount(int count){
            currentCount = count;
        }
        @Override
        public void onClick(View v) {
            ItemDetails itemDetails = itemDetailsNameMap.get(name.getText().toString());
            if (v.getId() == R.id.plus_item) {
                int itemCount = Integer.parseInt(count.getText().toString());
                if (itemCount >= 50) return;
                setCurrentCount(++itemCount);
                count.setText(String.valueOf(itemCount));
                Basket.addItem(itemDetails);
            } else if (v.getId() == R.id.minus_item) {
                int itemCount = Integer.parseInt(count.getText().toString());
                if (itemCount == 0) {
                    return;
                }
                setCurrentCount(--itemCount);
                count.setText(String.valueOf(itemCount));
                Basket.removeItem(itemDetails);
            }

            basketView.setText("₹"+Basket.getBasketValue());
        }
    }
}