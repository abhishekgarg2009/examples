package org.tensorflow.lite.examples.classification.storage;

public class MyItemList {
    private int price;
    private int count;
    private String itemName;
    private String id;
    private int imageResource;
    private ItemDetails itemDetails;

    public MyItemList(int price, String  itemName, int count, int imageResource, ItemDetails itemDetails, String id){
        this.price = price;
        this.itemName = itemName;
        this.count = count;
        this.imageResource = imageResource;
        this.itemDetails = itemDetails;
        this.id = id;
    }

    public int getPrice(){
        return price;
    }

    public String getId(){
        return id;
    }

    public int getCount(){
        return count;
    }

    public int getImageResource(){
        return imageResource;
    }

    public String getItemName(){
        return itemName;
    }

    public ItemDetails getItemDetails() { return itemDetails; }
}
