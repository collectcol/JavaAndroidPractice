package com.example.googlemaps_ex2.view;

import static com.example.googlemaps_ex2.utill.Permission.REQUEST_LOCATION_PERMISSION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemaps_ex2.R;
import com.example.googlemaps_ex2.model.ParentRoot;
import com.example.googlemaps_ex2.model.Root;
import com.example.googlemaps_ex2.utill.DBHelper;
import com.example.googlemaps_ex2.utill.Permission;
import com.example.googlemaps_ex2.view.dialog.MarkerAddDialog;
import com.example.googlemaps_ex2.view.dialog.RootDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    public static final String ROOT_NAME = "";
    private Permission permission;
    private Menu menu;
    private DBHelper dbHelper;
    private LatLng currentLatLng;
    private GoogleMap gMap;
    private String currentRoot;
    private List<Marker> currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        dbHelper = new DBHelper(this);

        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        permission = new Permission(this);
        permission.permissionCheck();
        currentLatLng = permission.currentLatLng;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 승인된 경우
                permission.checkLocationPermission(); // 위치 정보 가져오는 메서드 호출
            } else {
                // 위치 권한이 거부된 경우
                Toast.makeText(this, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                this.finish(); // 앱 종료
            }
        }
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
        ParentRoot pr = (ParentRoot) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
        if ( pr == null )
        {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, gMap.getCameraPosition().zoom));

        showDialogToAddMarker(latLng);
    }

    private void showDialogToAddMarker(final LatLng latLng)
    {
        MarkerAddDialog markerAddDialog = new MarkerAddDialog(this);

        markerAddDialog.showDialog();
        markerAddDialog.setListener(new MarkerAddDialog.DialogListener()
        {
            @Override
            public void onPositiveButtonClick(String title, String snippet)
            {
                if (title.isEmpty()){
                    Toast.makeText(MainActivity.this, "장소를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( snippet.isEmpty() )
                {
                    Toast.makeText(MainActivity.this, "설명을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                addMarkerToMap(latLng, title, snippet);
            }
            @Override
            public void onNegativeButtonClick()
            {

            }
        });
    }

    private void addMarkerToMap(LatLng latLng, String markerTitle, String markerSnippet)
    {
        ParentRoot pr = (ParentRoot) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
        if ( pr == null )
        {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( menu.getItem(1).getTitle().toString().equals("") )
        {
            Toast.makeText(this, "먼저 루트를 선택해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.title(markerTitle);
        options.position(latLng);
        options.snippet(markerSnippet);

        // 맵에 마커 추가하고 List에 마커 추가하기
        Marker marker = gMap.addMarker(options);
        currentMarker.add(marker);
        marker.showInfoWindow();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude + 0.0001, latLng.longitude + 0.0001), gMap.getCameraPosition().zoom));

        String markerData = markerDescription + "|" + latLng;
        pr.getChildRoots().add()

        // 마커 이름에 마커데이터 추가
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(markerTitle, markerData);
        editor.apply();

        try
        {
            // 해당 루트 불러와서 마커 이름들 json 형식으로 추가저장하기
            String root = mRootMenu.getItem(0).getTitle().toString();
            String markersInRoot = sharedPreferences.getString(root, "");

            if ( markersInRoot.isEmpty() )
            {
                markersInRoot = markerTitle;
            } else
            {
                markersInRoot = markersInRoot + "|" + markerTitle;
            }

            editor.putString(root, markersInRoot);
            editor.apply();
        } catch ( Exception e )
        {

        }
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
//                showMarkerDeleteDialog(marker);
            }
        });

        // Infowindow 클릭시 마커 수정
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker)
            {
//                showMarkerUpdateDialog(marker);
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
            ParentRoot parentRoot = ( ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
            if ( parentRoot != null )
            {
                menu.getItem(0).setTitle(parentRoot.getChildRoots().get(0).getRootName());
            }
        } catch ( Exception e )
        {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if ( id == R.id.currentLocation )
        {
            gMap.setMyLocationEnabled(!gMap.isMyLocationEnabled());
        } else if ( id == R.id.mapType_hybrid )
        {
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        } else if ( id == R.id.mapType_normal )
        {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if ( id == R.id.roots )
        {
            // 설정된 루트들 보기
            showRootsDialog();

        } else if ( id == R.id.root_add )
        {
            // 클릭시 루트 추가하기
//            showRootInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRootsDialog(){
        ParentRoot pr = (ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
        if(pr == null){
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        RootDialog rootDialog = new RootDialog(this, screenHeight);

        Root[] rootArray = pr.getChildRoots();
        ArrayList<String> rootNameArray = new ArrayList<>();

        for (int i = 0; i < rootArray.length; i++){
            rootNameArray.add(rootArray[i].getRootName());
        }

        rootDialog.showDialog(rootNameArray);
        rootDialog.setListener(new RootDialog.DialogListener()
        {
            @Override
            public void onItemClick(String selectedItem)
            {
                menu.getItem(1).setTitle(selectedItem);
                currentRoot = selectedItem;
                gMapMarkerClear();
//                marker_Load(selectedItem);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }

            @Override
            public void onItemLongClick(String selectedItem)
            {

            }
        });
    }
    private void gMapMarkerClear()
    {
        for (int i = 0; i<currentMarker.length; i++){
            currentMarker[i].remove();
        }
//        for ( Marker marker : markerList )
//        {
//            marker.remove();
//        }
        currentMarker = null;
//        markerList.clear();
    }

    // 루트에 저장된 마커이름을 통해 마커 생성하고 넣기
//    private void marker_Load(String rootName)
//    {
//        String markerInRoot = sharedPreferences.getString(rootName, "");
//
//        if ( markerInRoot.isEmpty() )
//            return;
//
//        String[] markerTitles = markerInRoot.split("\\|");
//
//        if ( markerTitles.length == 0 )
//            return;
//
//        for ( String markerTitle : markerTitles )
//        {
//            if ( sharedPreferences.getString(markerTitle, "").isEmpty() )
//            {
//                continue;
//            }
//
//            String markerDatas = sharedPreferences.getString(markerTitle, "");
//            String[] markerData = markerDatas.split("\\|");
//
//            int startIndex = markerData[1].indexOf("(") + 1;
//            int endIndex = markerData[1].indexOf(")");
//            String latLngStr = markerData[1].substring(startIndex, endIndex);
//
//            String[] latLngParts = latLngStr.split(",");
//            double latitude = Double.parseDouble(latLngParts[0].trim());
//            double longitude = Double.parseDouble(latLngParts[1].trim());
//
//            MarkerOptions marker = new MarkerOptions()
//                    .title(markerTitle)
//                    .snippet(markerData[0])
//                    .position(new LatLng(latitude, longitude));
//
//            Marker _marker = gMap.addMarker(marker);
//            markerList.add(_marker);
//            _marker.showInfoWindow();
//        }
//    }
}