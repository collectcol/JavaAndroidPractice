package com.example.googlemaps_ex2.model;


import java.util.ArrayList;

public class Root
{
    private String RootName = "";
    private ArrayList<MarkerInRoot> MarkerList;

    public String getRootName()
    {
        return RootName;
    }

    public void setRootName(String rootName)
    {
        RootName = rootName;
    }

    public ArrayList<MarkerInRoot> getMarkerList()
    {
        if ( MarkerList == null )
        {
            MarkerList = new ArrayList<MarkerInRoot>();
        }
        return MarkerList;
    }

    public void setMarkerList(ArrayList<MarkerInRoot> markerList)
    {
        if ( MarkerList == null )
        {
            MarkerList = new ArrayList<MarkerInRoot>();
        }
        MarkerList = markerList;
    }
}
