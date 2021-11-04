package com.w3engineers.unicef.telemesh.data.local.db;

import androidx.room.TypeConverter;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class Converters {

    // having long as the parameter and returning Date type
    @TypeConverter
    @NonNull
    public static Date toDate(long dateLong) {
        return new Date(dateLong);
    }

    // opposite to 1st one
    @TypeConverter
    public static long fromDate(@NonNull Date date) {
        return date.getTime();
    }

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
