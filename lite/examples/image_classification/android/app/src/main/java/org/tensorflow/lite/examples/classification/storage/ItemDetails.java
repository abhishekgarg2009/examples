package org.tensorflow.lite.examples.classification.storage;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ItemDetails {
    private String Id = "Default Brown Bag";
    private int price = 50;
    private String imageUrl = "brown";

    public ItemDetails(String id, int price, String imageUrl) {
        Id = id;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public ItemDetails() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
