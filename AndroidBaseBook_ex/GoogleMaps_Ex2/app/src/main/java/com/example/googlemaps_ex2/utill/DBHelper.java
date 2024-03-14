package com.example.googlemaps_ex2.utill;

import static com.example.googlemaps_ex2.view.MainActivity.ROOT_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class DBHelper
{
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public DBHelper(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

    }

    public Object SharedSelect(String key, Object val)
    {
        String v = sharedPreferences.getString(key, "");
        Object o = null;
        if ( ! v.isEmpty() )
        {
            o = gson.fromJson(v, ( Class ) val);
        } else {
            try {
                o = ((Class)val).newInstance(); // val을 이용하여 새로운 객체 생성
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public void SharedInsert(String key, Object o)
    {
        String json = gson.toJson(o);

        editor.putString(key, json);
        editor.apply();
    }

    public void SharedUpdate(String key)
    {
        String v = sharedPreferences.getString(key, "");
    }
}
