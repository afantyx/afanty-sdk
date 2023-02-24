package com.afanty.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JSONUtils {

    public static JSONArray convertArr2JSON(List list){
        if (list == null || list.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (Object str:list){
            jsonArray.put(str);
        }
        return jsonArray;
    }
    public static List<Object> convertJSON2Arr(JSONArray array){
        List<Object> list = new ArrayList<>();
        try {
            if (array == null)
                return list;
            for (int i = 0; i < array.length(); i++) {
                list.add(array.get(i));
            }
        }catch (JSONException ignore){}
        return list;
    }
}
