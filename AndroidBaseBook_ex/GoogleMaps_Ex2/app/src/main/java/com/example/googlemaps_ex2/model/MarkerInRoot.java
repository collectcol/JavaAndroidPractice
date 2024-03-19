package com.example.googlemaps_ex2.model;

import com.google.android.gms.maps.model.LatLng;

public class MarkerInRoot{
    private String Title;
    private com.google.android.gms.maps.model.LatLng LatLng;
    private String Snippet;
    private int Index;

    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

    public com.google.android.gms.maps.model.LatLng getLatLng()
    {
        return LatLng;
    }

    public void setLatLng(com.google.android.gms.maps.model.LatLng latLng)
    {
        LatLng = latLng;
    }

    public String getSnippet()
    {
        return Snippet;
    }

    public void setSnippet(String snippet)
    {
        Snippet = snippet;
    }

    public int getIndex()
    {
        return Index;
    }

    public void setIndex(int index)
    {
        Index = index;
    }
}
