package com.example.googlemaps_ex2.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class Root
{
    private String RootName = "";
    private ArrayList<Marker> MarkerList;

    public String getRootName()
    {
        return RootName;
    }

    public void setRootName(String rootName)
    {
        RootName = rootName;
    }

    public ArrayList<Marker> getMarkerList()
    {
        if (MarkerList == null){
            MarkerList = new ArrayList<Marker>();
        }
        return MarkerList;
    }

    public void setMarkerList(ArrayList<Marker> markerList)
    {
        if (MarkerList == null){
            MarkerList = new ArrayList<Marker>();
        }
        MarkerList = markerList;
    }
}
