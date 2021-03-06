package org.tensorflow.lite.examples.classification.storage;

import android.content.Context;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProductDetails {
    public static void populateProductDetails(Context context) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                context.getAssets().open("lib/productDetails.csv")
        ))) {
//            CSVReader reader = new CSVReader(new FileReader("lib/productDetails.csv"));

            List resultList = new ArrayList();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                ItemDetails itemDetails = new ItemDetails();
                itemDetails.setId(nextLine[0]);
                itemDetails.setDisplayName(nextLine[2]);
                itemDetails.setCluster(nextLine[1]);
                itemDetails.setPrice(Integer.parseInt(nextLine[3]));
                itemDetails.setImageUrl(nextLine[4]);
                SharedPreferenceManager.addItem(context, itemDetails);
                ClusterMapper.addItem(itemDetails);
            }
        } catch (IOException e) {
            System.out.println("Caught Exception while reading csv: " + e);
        }
    }

}
