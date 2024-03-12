package com.example.googlemaps_ex2.utill;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.googlemaps_ex2.Global;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class Permission extends AppCompatActivity
{
    private Global GlobalContext;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public LatLng currentLatLng;

    public Permission()
    {
        this.GlobalContext = ( Global ) getApplicationContext();
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void permissionCheck()
    {
        // 위치 권한이 허용되어 있는지 확인
        if ( ActivityCompat.checkSelfPermission(GlobalContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(GlobalContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location ->
                    {
                        if ( location != null )
                        {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        } else
                        {
                            Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, e ->
                    {
                        Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    });
        } else
        {
            Toast.makeText(GlobalContext, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST_LOCATION_PERMISSION )
        {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                // 위치 권한이 승인된 경우
            } else
            {
                // 위치 권한이 거부된 경우
                Toast.makeText(this, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish(); // 앱 종료
            }
        }
    }
}
