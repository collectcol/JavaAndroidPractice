package com.example.googlemaps_ex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressWarnings( "deprecation" )
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap gMap;
    MapFragment mapFrag;
    LatLng currentLocation;
    Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("구글 지도");
        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 권한 확인 및 요청
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);
        } else
        {
            // 위치 권한이 이미 승인된 경우
            getLastKnownLocationAndMoveCamera();
        }
    }

    private void getLastKnownLocationAndMoveCamera()
    {
        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 없는 경우 권한 요청
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 위치 설정
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location ->
                {
                    if ( location != null )
                    {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // 15는 줌 레벨, 원하는 값으로 조절 가능
                        // 마커 위치 업데이트
                        updateMarker(currentLocation);
                    } else
                    {
                        Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e ->
                {
                    // 위치 가져오기 실패 시 처리할 작업 추가
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 승인된 경우
                getLastKnownLocationAndMoveCamera();
            } else {
                // 위치 권한이 거부된 경우
                Toast.makeText(this, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish(); // 앱 종료
            }
        }
    }

    private void updateMarker(LatLng currentLocation) {
        if (gMap != null) {
            // 기존 마커 제거
            if (currentMarker != null)
                currentMarker.remove();

            // 새로운 마커 추가
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title("나");
            markerOptions.snippet("현재위치");
            currentMarker = gMap.addMarker(markerOptions);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap map)
    {
        gMap = map;
        getLastKnownLocationAndMoveCamera();
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLocation);
//        markerOptions.title("나");
//        markerOptions.snippet("현재위치");
//
//        gMap.addMarker(markerOptions);
        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if ( id == R.id.mapType_hybrid )
        {
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        } else if ( id == R.id.mapType_normal )
        {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}