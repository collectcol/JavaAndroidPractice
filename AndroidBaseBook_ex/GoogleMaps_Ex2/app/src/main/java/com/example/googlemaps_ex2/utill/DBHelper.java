package com.example.googlemaps_ex2.utill;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemaps_ex2.Global;

import org.json.JSONArray;
import org.json.JSONException;

public class DBHelper extends AppCompatActivity
{
    private Global GlobalContext;
    private static SharedPreferences sharedPreferences;

    public DBHelper()
    {
        this.GlobalContext = ( Global ) getApplicationContext();
        sharedPreferences = GlobalContext.getSharedPreferences(GlobalContext.ROOT_NAME, MODE_PRIVATE);
    }

    public static JSONArray getJSONArray(String key)
    {
        try
        {
            String v = sharedPreferences.getString(key, "");
            if ( ! v.isEmpty() )
            {
                JSONArray array = new JSONArray(v);

            }
        } catch ( Exception e )
        {

        }
        return new JSONArray();
    }
}
