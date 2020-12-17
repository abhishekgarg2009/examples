package org.tensorflow.lite.examples.classification.storage;

public class ItemDetails {
    private String id = "Default Popcorn";
    private int price = 50;
    private String imageUrl = "pop";
    private String displayName = "Popcorn";

    public ItemDetails(String id, int price, String imageUrl, String displayName) {
        this.id = id;
        this.price = price;
        this.imageUrl = imageUrl;
        this.displayName = displayName;
    }

    public ItemDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
