package com.example.googlemaps_ex2.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemaps_ex2.R;
import com.example.googlemaps_ex2.model.MarkerInRoot;
import com.example.googlemaps_ex2.model.ParentRoot;
import com.example.googlemaps_ex2.model.Root;
import com.example.googlemaps_ex2.utill.DBHelper;
import com.example.googlemaps_ex2.view.dialog.MarkerAddDialog;
import com.example.googlemaps_ex2.view.dialog.RootDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    public static final String ROOT_NAME = "";
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private Menu menu;
    private DBHelper dbHelper;
    private LatLng currentLatLng;
    private GoogleMap gMap;
    private String currentRoot = "";
    private Root r;


    private FusedLocationProviderClient fusedLocationProviderClient;

    ArrayList<Marker> currentMarker;

    public MainActivity()
    {
        r = new Root();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        dbHelper = new DBHelper(this);

        SupportMapFragment mapFragment = ( SupportMapFragment ) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        permissionCheck();

//        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

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

    // region 위치권한 관련 로직

    public void permissionCheck()
    {
        // 위치 권한이 허용되어 있는지 확인
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);
        } else
        {
            checkLocationPermission();
        }
    }

    // 위치 권한 확인 메서드
    public void checkLocationPermission()
    {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location ->
                {
                    if ( location != null )
                    {
//                        onMapReady(gMap);
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    } else
                    {
                        LocationRequest mRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(300)
                                .setFastestInterval(200);

                        LocationCallback mLocationCallback = new LocationCallback()
                        {
                            @Override
                            public void onLocationResult(LocationResult locationResult)
                            {
                                if ( locationResult == null )
                                {
                                    return;
                                }
                                for ( Location location : locationResult.getLocations() )
                                {
                                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    fusedLocationProviderClient.removeLocationUpdates(this);
                                }
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(mRequest, mLocationCallback, null);
//                        Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                })
                .addOnFailureListener(this, e ->
                {
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                });
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
                checkLocationPermission(); // 위치 정보 가져오는 메서드 호출
            } else
            {
                // 위치 권한이 거부된 경우
                Toast.makeText(this, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                this.finish(); // 앱 종료
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    // endregion

    @Override
    protected void onResume()
    {
        super.onResume();
//        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
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
        permissionCheck();

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
        ParentRoot pr = ( ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);

        if ( pr.getRootList().size() == 0 )
        {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, gMap.getCameraPosition().zoom));

        showDialogToAddMarker(latLng);
    }


    // region 메뉴관련 로직
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;

        try
        {
            ParentRoot pr = ( ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);

            if ( pr.getRootList().size() != 0 )
            {
                menu.getItem(1).setTitle(pr.getRootList().get(0).getRootName());
                currentRoot = pr.getRootList().get(0).getRootName();
            } else
            {
                menu.getItem(1).setTitle(currentRoot);
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
            gMap.setMyLocationEnabled(! gMap.isMyLocationEnabled());
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
            showRootInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region 루트 관련 로직
    private void showRootInputDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("루트 설정");
        builder.setMessage("새로운 루트를 설정해 주세요");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String itemName = input.getText().toString();
                if ( ! itemName.isEmpty() )
                {
                    rootAdd(itemName);
                } else
                {
                    Toast.makeText(MainActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void rootAdd(String rootName)
    {
        ParentRoot pr = ( ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
        Root r = new Root();

        if ( pr.getRootList().size() != 0 )
        {
            for ( int i = 0; i < pr.getRootList().size(); i++ )
            {
                if ( pr.getRootList().get(i).getRootName().equals(rootName) )
                {
                    Toast.makeText(this, "중복된 루트가 존재합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        r.setRootName(rootName);
        pr.getRootList().add(r);
        dbHelper.SharedInsert(ROOT_NAME, pr);
        currentRoot = rootName;
        menu.getItem(1).setTitle(currentRoot);

        // 모든 마커 지우기
        gMapMarkerClear();
    }

    private void showRootsDialog()
    {
        ParentRoot pr = ( ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
        if ( pr.getRootList().size() == 0 )
        {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        RootDialog rootDialog = new RootDialog(this, screenHeight);

        ArrayList<String> rootNameArray = new ArrayList<>();

        for ( int i = 0; i < pr.getRootList().size(); i++ )
        {
            rootNameArray.add(pr.getRootList().get(i).getRootName());
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
                markerLoad(selectedItem);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }

            @Override
            public void onItemLongClick(String selectedItem)
            {

            }
        });
    }

    private void markerLoad(String rootName)
    {
        Root r = ( Root ) dbHelper.SharedSelect(rootName, Root.class);
        ArrayList<MarkerInRoot> markerList = r.getMarkerList();

        for ( int i = 0; i < markerList.size(); i++ )
        {
            MarkerOptions options = new MarkerOptions();
            options.title(markerList.get(i).getTitle());
            options.position(markerList.get(i).getLatLng());
            options.snippet(markerList.get(i).getSnippet());

            Marker marker = gMap.addMarker(options);
            currentMarker.add(marker);
        }
    }
    // endregion

    // region 마커 관련 로직
    private void showDialogToAddMarker(final LatLng latLng)
    {
        MarkerAddDialog markerAddDialog = new MarkerAddDialog(this);

        markerAddDialog.showDialog();
        markerAddDialog.setListener(new MarkerAddDialog.DialogListener()
        {
            @Override
            public void onPositiveButtonClick(String title, String snippet)
            {
                if ( title.isEmpty() )
                {
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
        ParentRoot pr = ( ParentRoot ) dbHelper.SharedSelect(ROOT_NAME, ParentRoot.class);
        if ( pr.getRootList().size() == 0 )
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

        // 맵에 마커 추가하고 Root의 MarkerList에 마커 추가하기
        Marker marker = gMap.addMarker(options);
        marker.showInfoWindow();

        Root r = new Root();
        MarkerInRoot m = new MarkerInRoot();
        m.setTitle(markerTitle);
        m.setLatLng(latLng);
        m.setSnippet(markerSnippet);
        m.setIndex(r.getMarkerList().size() + 1);

        r.getMarkerList().add(m);
        r.setRootName(currentRoot);

//        r.setMarkerValue(marker);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude + 0.0001, latLng.longitude + 0.0001), gMap.getCameraPosition().zoom));

        // 현재 마커 리스트에 마커 추가
        currentMarker.add(marker);

        // 마커 이름에 마커데이터 추가
        dbHelper.SharedInsert(currentRoot, r);
    }

    private void gMapMarkerClear()
    {
        if ( currentMarker != null )
        {
            for ( int i = 0; i < currentMarker.size(); i++ )
            {
                currentMarker.get(i).remove();
            }
            currentMarker = null;
        }
    }
    // endregion
}