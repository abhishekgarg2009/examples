package org.tensorflow.lite.examples.classification.storage;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    List<ItemDetails> items = new ArrayList<>();
    int basketValue = 0;

    public Basket(){ }

    public Basket(List<ItemDetails> items, int basketValue){
        this.items = items;
        this.basketValue = basketValue;
    }

    public int getBasketValue() {
        return basketValue;
    }

    public void setBasketValue(int price) {
        this.basketValue = price;
    }

    public void addBasketValue(int price) {
        this.basketValue = basketValue + price;
    }

    public void addItem(ItemDetails itemDetails){
        this.items.add(itemDetails);
        this.basketValue = basketValue + itemDetails.getPrice();
    }

    public void removeItem(ItemDetails itemDetails){
        this.items.remove(itemDetails);
        this.basketValue = basketValue - itemDetails.getPrice();
    }
}
