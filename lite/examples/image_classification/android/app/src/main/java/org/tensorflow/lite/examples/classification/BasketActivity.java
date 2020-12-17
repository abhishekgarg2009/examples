package org.tensorflow.lite.examples.classification;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.classification.storage.Basket;
import org.tensorflow.lite.examples.classification.storage.ItemDetails;
import org.tensorflow.lite.examples.classification.storage.MyItemList;
import org.tensorflow.lite.examples.classification.storage.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BasketActivity extends Activity{
    protected TextView basketPriceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basket);

        ImageView next = findViewById(R.id.new_id);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.setBackgroundColor(Color.parseColor("#344622"));
                Intent intent = new Intent(BasketActivity.this, ClassifierActivity.class);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });

        basketPriceView = findViewById(R.id.basket1);
        basketPriceView.setText("₹"+Basket.getBasketValue());

        List<MyItemList> listData = new ArrayList<>();

        Map<String, Integer> itemIdVsCount = Basket.getItemIdVsCount();
        for(String id : itemIdVsCount.keySet()){
            ItemDetails itemDetails = SharedPreferenceManager.getItem(getApplicationContext(),id);

            int count = id.equals("default") ? 0 : itemIdVsCount.get(id);

            MyItemList myList = new MyItemList(itemDetails.getPrice(), itemDetails.getDisplayName(),
                    count, getImageResourceByName(itemDetails.getImageUrl()), itemDetails, id);
            listData.add(myList);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyListAdapter(listData, basketPriceView));
    }

    private int getImageResourceByName(String imageName) {
        return getResources().getIdentifier(imageName, "drawable", this.getPackageName());
    }
}
