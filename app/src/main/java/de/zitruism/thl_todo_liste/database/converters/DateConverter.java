package de.zitruism.thl_todo_liste.database.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.room.TypeConverter;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromList(List<?> list){
        if(list == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for(int i=0; i < list.size(); i++){
            sb.append(list.get(i));
            if(i < list.size()-1)
                sb.append("_");
        }

        return sb.toString();
    }

    @TypeConverter
    public static List<String> toList(String listString){
        if(listString == null)
            return null;
        String[] parts = listString.split("_");
        return new ArrayList<>(Arrays.asList(parts));
    }
}