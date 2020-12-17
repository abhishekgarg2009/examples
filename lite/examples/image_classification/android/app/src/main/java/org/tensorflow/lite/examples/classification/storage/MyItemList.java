package org.tensorflow.lite.examples.classification.storage;

public class MyItemList {
    private int price;
    private int count;
    private String itemName;
    private int imageResource;

    public MyItemList(int price, String  itemName, int count, int imageResource){
        this.price = price;
        this.itemName = itemName;
        this.count = count;
        this.imageResource = imageResource;
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
}
