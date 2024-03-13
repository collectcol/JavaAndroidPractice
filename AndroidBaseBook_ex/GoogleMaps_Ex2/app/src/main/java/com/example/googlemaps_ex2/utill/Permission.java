package com.example.googlemaps_ex2.utill;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class Permission extends Activity
{
    private Activity activity;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public LatLng currentLatLng;

    public Permission(Activity activity)
    {
        this.activity = activity;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void permissionCheck()
    {
        // 위치 권한이 허용되어 있는지 확인
        if ( ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);
        } else
        {
            checkLocationPermission();
        }
    }

    // 위치 권한 확인 메서드
    public void checkLocationPermission()
    {
        // 위치 권한이 있는 경우에만 위치 정보 요청
        if ( ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            Toast.makeText(activity, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            activity.finish(); // 앱 종료
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, location ->
                {
                    if ( location != null )
                    {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    } else
                    {
                        Toast.makeText(activity, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(activity, e ->
                {
                    Toast.makeText(activity, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 위치 권한이 승인된 경우
//                checkLocationPermission(); // 위치 정보 가져오는 메서드 호출
//            } else {
//                // 위치 권한이 거부된 경우
//                Toast.makeText(activity, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
//                activity.finish(); // 앱 종료
//            }
//        }
//    }
}
