package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Azizul Islam on 8/10/21.
 */
public class ListConverter {
    @TypeConverter
    public static ArrayList<String> fromString(String jsonStr){
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(jsonStr, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
