package org.tensorflow.lite.examples.classification.storage;

public class ItemDetails {
    private String id = "default";
    private int price = 0;
    private String imageUrl = "brown";
    private String displayName = "default";
    private String cluster = "default";

    public ItemDetails(String id, int price, String imageUrl, String displayName, String cluster) {
        this.id = id;
        this.price = price;
        this.imageUrl = imageUrl;
        this.displayName = displayName;
        this.cluster = cluster;
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

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
