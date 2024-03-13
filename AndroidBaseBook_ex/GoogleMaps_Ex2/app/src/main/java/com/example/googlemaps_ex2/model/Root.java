package com.example.googlemaps_ex2.model;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Root
{
    private String RootName;
    private List<Marker> Marker;

    public String getRootName()
    {
        return RootName;
    }

    public void setRootName(String rootName)
    {
        RootName = rootName;
    }

    public List<Root.Marker> getMarker()
    {
        return Marker;
    }

    public void setMarker(List<Root.Marker> marker)
    {
        Marker = marker;
    }

    class Marker{
        private String MarkerName;
        private String MarkerSnippet;
        private LatLng MarkerLatLng;
        public String getMarkerName()
        {
            return MarkerName;
        }

        public void setMarkerName(String markerName)
        {
            MarkerName = markerName;
        }

        public String getMarkerSnippet()
        {
            return MarkerSnippet;
        }

        public void setMarkerSnippet(String markerSnippet)
        {
            MarkerSnippet = markerSnippet;
        }

        public LatLng getMarkerLatLng()
        {
            return MarkerLatLng;
        }

        public void setMarkerLatLng(LatLng markerLatLng)
        {
            MarkerLatLng = markerLatLng;
        }
    }
}
