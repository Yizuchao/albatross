package com.yogi.albatross.utils;

import java.util.Collection;
import java.util.Objects;

public class CollectionUtils {
    public static boolean isEmpty(Collection collection){
        return collection==null || collection.isEmpty();
    }

    public static void addAll(Collection addTargetCollection ,Collection addCollection){
        if(Objects.isNull(addCollection)){
            return;
        }
        addTargetCollection.addAll(addCollection);
    }
}
