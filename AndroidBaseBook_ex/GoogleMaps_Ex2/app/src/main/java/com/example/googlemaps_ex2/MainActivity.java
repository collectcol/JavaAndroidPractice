package com.example.googlemaps_ex2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemaps_ex2.utill.DBHelper;
import com.example.googlemaps_ex2.utill.Permission;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    private Permission permission;
    private boolean permissionCheck;
    private Menu menu;
    private DBHelper dbHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Global GlobalContext;
    private LatLng currentLatLng;
    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        dbHelper = new DBHelper();

        GlobalContext = ( Global ) getApplication();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        permission = new Permission();
        permission.permissionCheck();
        currentLatLng = permission.currentLatLng;

        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gMap.setMyLocationEnabled(true);

        Button searchButton = ( Button ) findViewById(R.id.searchButton);
        EditText searchEditText = ( EditText ) findViewById(R.id.searchEditText);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String locationName = searchEditText.getText().toString();
                moveCameraToSearchedLocation(locationName);
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if ( actionId == EditorInfo.IME_ACTION_SEARCH )
                {
                    // 사용자가 입력한 검색어 가져오기
                    String locationName = searchEditText.getText().toString();
                    // 사용자가 입력한 검색어를 가지고 해당 위치로 이동하는 함수 호출
                    moveCameraToSearchedLocation(locationName);
                    return true;
                }
                return false;
            }
        });
    }

    // 사용자가 검색한 장소의 주소를 가져와서 해당 장소로 이동하는 메소드
    private void moveCameraToSearchedLocation(String locationName)
    {
        // Geocoder를 사용하여 주소를 좌표로 변환
        Geocoder geocoder = new Geocoder(this);
        try
        {
            // 주소로부터 좌표 목록을 가져옴
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if ( addresses != null && ! addresses.isEmpty() )
            {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                // 해당 좌표로 지도 이동
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, gMap.getCameraPosition().zoom)); // 줌 레벨 조절 가능
                // 해당 위치에 마커 추가
            } else
            {
                // 검색된 결과가 없는 경우 처리
                Toast.makeText(this, "검색된 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch ( Exception e )
        {
            e.printStackTrace();
            // 예외 발생 시 처리
            Toast.makeText(this, "검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng)
    {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        gMap.setOnMapClickListener(this);

        // InfoWindow 길게 누를시 마커 삭제
        gMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener()
        {
            @Override
            public void onInfoWindowLongClick(@NonNull Marker marker)
            {
                showMarkerDeleteDialog(marker);
            }
        });

        // Infowindow 클릭시 마커 수정
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker)
            {
                showMarkerUpdateDialog(marker);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;

        try
        {
            String titleCheck = sharedPreferences.getString(ROOT_NAME, "");
            if ( ! titleCheck.isEmpty() )
            {
                JSONArray array = new JSONArray(titleCheck);
                String baseRootName = array.getString(0);

                mRootMenu.getItem(0).setTitle(baseRootName);
            }
        } catch ( Exception e )
        {

        }
        return true;
    }
}