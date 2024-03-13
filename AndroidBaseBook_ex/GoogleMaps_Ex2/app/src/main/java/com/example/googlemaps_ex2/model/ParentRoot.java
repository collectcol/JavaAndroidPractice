package com.example.googlemaps_ex2.model;

import java.util.List;

public class ParentRoot
{
    private List<Root> ChildRoots;

    public List<Root> getChildRoots()
    {
        return ChildRoots;
    }

    public void setChildRoots(List<Root> childRoots)
    {
        ChildRoots = childRoots;
    }
}
