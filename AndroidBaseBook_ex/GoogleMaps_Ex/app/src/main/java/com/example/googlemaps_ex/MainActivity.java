package com.example.googlemaps_ex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    GoogleMap gMap;
    MapFragment mapFrag;
    LatLng AirPort = new LatLng(37.556, 126.97);
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("구글 지도");
        SupportMapFragment mapFragment = ( SupportMapFragment )getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map)
    {
        gMap = map;
        LatLng SEOUL = new LatLng(37.556, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국 수도");

        gMap.addMarker(markerOptions);
//        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "위성 지도");
        menu.add(0, 2, 0, "일반 지도");
        menu.add(0, 3, 0, "공항 바로가기");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()){
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case 3:
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AirPort, 15));
        }
        return super.onOptionsItemSelected(item);
    }
}