package com.example.googlemaps_ex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String ROOT_NAME = "ROOT_NAME";
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Menu mRootMenu;
    GoogleMap gMap;
    MapFragment mapFrag;
    LatLng currentLocation;
    Marker currentMarker;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("구글 지도");
        sharedPreferences = getSharedPreferences(ROOT_NAME, MODE_PRIVATE);
        if (sharedPreferences == null) {
            // SharedPreferences가 null이면 새로 생성
//            sharedPreferences = getSharedPreferences(ROOT_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ROOT_NAME, ""); // 기본값으로 빈 문자열 설정
            editor.apply();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 위치 권한 확인 및 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // 위치 권한이 이미 승인된 경우
            getLastKnownLocationAndMoveCamera();
        }
    }

    private void getLastKnownLocationAndMoveCamera() {
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
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // 15는 줌 레벨, 원하는 값으로 조절 가능
                        // 마커 위치 업데이트
                        updateMarker(currentLocation);
                    } else {
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
    public void onMapReady(@NonNull GoogleMap map) {
        gMap = map;
        getLastKnownLocationAndMoveCamera();
        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        gMap.setOnMapClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        mRootMenu = menu;

        try {
            String titleCheck = sharedPreferences.getString(ROOT_NAME, "");
            if (!titleCheck.isEmpty()) {
                JSONArray array = new JSONArray(titleCheck);
                String baseRootName = array.getString(0);

                mRootMenu.getItem(0).setTitle(baseRootName);
            }
        } catch (Exception e) {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mapType_hybrid) {
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        } else if (id == R.id.mapType_normal) {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if (id == R.id.roots) {
            /// TODO 지금 가지고 있는 루트들 드랍다운으로 보여주고 해당 루트 클릭시 지도 마커 업데이트, 해당 루트로 title 업데이트
            // 설정된 루트들 보기
            showRootsDialog();

        } else if (id == R.id.root_add) {
            // 클릭시 루트 추가하기
            showRootInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRootsDialog() {
        String rootListString = sharedPreferences.getString(ROOT_NAME, "");
        if (rootListString.isEmpty()) {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        RootDialog rootDialog = new RootDialog(this, screenHeight);

        ArrayList<String> rootList = new ArrayList<>();

        try {
            // JSON 문자열을 JSONArray로 파싱하여 각 항목을 ArrayList에 추가
            JSONArray jsonArray = new JSONArray(rootListString);
            for (int i = 0; i < jsonArray.length(); i++) {
                String rootName = jsonArray.getString(i);
                rootList.add(rootName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rootDialog.showDialog(rootList);
        rootDialog.setListener(new RootDialog.DialogListener() {
            @Override
            public void onItemClick(String selectedItem) {
                /// 클릭된 루트 setTitle로 텍스트 변경, 루트값으로 마커들 업데이트
                mRootMenu.getItem(0).setTitle(selectedItem);
            }
        });
    }

    // 사용자에게 루트메뉴 이름 입력받기
    private void showRootInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("루트 설정");
        builder.setMessage("새로운 루트를 설정해 주세요");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String itemName = input.getText().toString();
                if (!itemName.isEmpty()) {
                    rootAdd(itemName);
                } else {
                    Toast.makeText(MainActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void rootAdd(String rootName) {
        String rootListString = sharedPreferences.getString(ROOT_NAME, "");

        try {
            if (rootListString.isEmpty()) {
                JSONArray rootArray = new JSONArray();
                rootArray.put(rootName);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ROOT_NAME, rootArray.toString());
                editor.apply();
            } else {
                JSONArray rootArray = new JSONArray(rootListString);

                boolean isDuplicate = false;
                for (int i = 0; i < rootArray.length(); i++) {
                    String existingRoot = rootArray.getString(i);
                    if (existingRoot.equals(rootName)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("중복된 루트!");
                        builder.setMessage("이미 등록된 루트 입니다.");
                        builder.show();
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    rootArray.put(rootName);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ROOT_NAME, rootArray.toString());
                    editor.apply();

                    mRootMenu.getItem(0).setTitle(rootName);
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        String root = sharedPreferences.getString("ROOT_NAME", "");
        if (root.isEmpty()) {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        showDialogToAddMarker(latLng);
    }

    private void showDialogToAddMarker(final LatLng latLng) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("마커 추가");

        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.marker_dialog, null);
        EditText markerTitle = (EditText) findViewById(R.id.marker_title);
        EditText markerDescription = (EditText) findViewById(R.id.marker_decription);

        builder.setView(dialogView);

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = markerTitle.getText().toString();
                String description = markerDescription.getText().toString();
                addMarkerToMap(latLng, title, description);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void addMarkerToMap(LatLng latLng, String markerTitle, String markerDescription) {
        String rootListString = sharedPreferences.getString(ROOT_NAME, "");
        if (rootListString.isEmpty()) {
            Toast.makeText(this, "먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mRootMenu.getItem(0).getTitle().toString().equals("")) {
            Toast.makeText(this, "먼저 루트를 선택해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        MarkerOptions options = new MarkerOptions().position(latLng).title(markerTitle);
        options.snippet(markerDescription);
        gMap.addMarker(options);

        String markerData = markerTitle + "|" + markerDescription + "|" + latLng;
        JSONArray rootArray = new JSONArray();
        rootArray.put(markerData);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(markerTitle, rootArray.toString());
        editor.apply();

        try {
//            JSONArray _rootArray = new JSONArray(rootListString);
//            _rootArray.put(markerTitle);
            editor.putString(mRootMenu.getItem(0).getTitle().toString(), markerTitle);
            editor.apply();
        } catch (Exception e) {

        }


    }
}