package org.tensorflow.lite.examples.classification.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterMapper {
    private static Map<String, Set<String>> clusterVsItemIds = new HashMap<>();

    public static void addItem(ItemDetails itemDetails) {
        if (!clusterVsItemIds.containsKey(itemDetails.getCluster())) {
            clusterVsItemIds.put(itemDetails.getCluster(), new HashSet<>());
        }
        clusterVsItemIds.get(itemDetails.getCluster()).add(itemDetails.getId());
    }

    //TODO: Probably we can return based on some score.
    public static List<String> getSimilarProducts(ItemDetails itemDetails) {
        Set<String> ids = new HashSet<>(clusterVsItemIds.get(itemDetails.getCluster()));
        ids.remove(itemDetails.getId());
        return new ArrayList<>(ids);
    }
}
