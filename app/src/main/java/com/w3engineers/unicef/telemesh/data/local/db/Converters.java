package com.w3engineers.unicef.telemesh.data.local.db;

import androidx.room.TypeConverter;
import androidx.annotation.NonNull;

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
}
