package com.example.googlemaps_ex2.model;

import java.util.ArrayList;
import java.util.List;

public class ParentRoot
{
    private ArrayList<Root> RootList;

    public ArrayList<Root> getRootList()
    {
        if (RootList == null){
            RootList = new ArrayList<Root>();
        }
        return RootList;
    }

    public void setRootList(ArrayList<Root> rootList)
    {
        if (RootList == null){
            RootList = new ArrayList<Root>();
        }
        RootList = rootList;
    }
}
