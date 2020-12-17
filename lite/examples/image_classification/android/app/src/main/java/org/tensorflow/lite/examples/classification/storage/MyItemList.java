package org.tensorflow.lite.examples.classification.storage;

public class MyItemList {
    private int price;
    private int count;
    private String itemName;
    private int imageResource;
    private ItemDetails itemDetails;

    public MyItemList(int price, String  itemName, int count, int imageResource, ItemDetails itemDetails){
        this.price = price;
        this.itemName = itemName;
        this.count = count;
        this.imageResource = imageResource;
        this.itemDetails = itemDetails;
    }

    public int getPrice(){
        return price;
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
