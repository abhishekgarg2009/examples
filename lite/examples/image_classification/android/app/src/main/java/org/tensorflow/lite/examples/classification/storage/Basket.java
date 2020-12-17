package org.tensorflow.lite.examples.classification.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Basket {
    private static Map<String, Integer> itemIdVsCount = new HashMap<>();
    private static int basketValue = 0;

    public static int getBasketValue() {
        return basketValue;
    }

    public static void setBasketValue(int price) {
        basketValue = price;
    }

    public static void addBasketValue(int price) {
        basketValue += price;
    }

    public static Map<String, Integer> getItemIdVsCount(){
        return itemIdVsCount;
    }

    public static void addItem(ItemDetails itemDetails){
        if(!itemIdVsCount.containsKey(itemDetails.getId())) {
            itemIdVsCount.put(itemDetails.getId(), 0);
        }
        itemIdVsCount.put(itemDetails.getId(), itemIdVsCount.get(itemDetails.getId()) + 1);
        basketValue += itemDetails.getPrice();
    }

    public static void removeItem(ItemDetails itemDetails){
        if (itemIdVsCount.containsKey(itemDetails.getId())) {
            if (itemIdVsCount.get(itemDetails.getId()) > 0) {
                basketValue -= itemDetails.getPrice();
            }
            if(itemIdVsCount.get(itemDetails.getId()) > 1) {
                itemIdVsCount.put(itemDetails.getId(), itemIdVsCount.get(itemDetails.getId()) -1);
            } else {
                itemIdVsCount.remove(itemDetails.getId());
            }

        }
    }
}
