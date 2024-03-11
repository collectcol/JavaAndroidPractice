package com.example.googlemaps_ex;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String ROOT_NAME = "ROOT_NAME";
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Menu mRootMenu;
    GoogleMap gMap;
    MapFragment mapFrag;
    LatLng currentLocation;
    Marker currentMarker;
    List<Marker> markerList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String currentRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("우경이의 여행 지도");
        sharedPreferences = getSharedPreferences(ROOT_NAME, MODE_PRIVATE);
        if ( sharedPreferences == null )
        {
            // SharedPreferences가 null이면 새로 생성
//            sharedPreferences = getSharedPreferences(ROOT_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ROOT_NAME, ""); // 기본값으로 빈 문자열 설정
            editor.apply();
        }
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
        Button searchButton = (Button) findViewById(R.id.searchButton);
        EditText searchEditText = (EditText )findViewById(R.id.searchEditText);

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
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
    private void moveCameraToSearchedLocation(String locationName) {
        // Geocoder를 사용하여 주소를 좌표로 변환
        Geocoder geocoder = new Geocoder(this);
        try {
            // 주소로부터 좌표 목록을 가져옴
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                // 해당 좌표로 지도 이동
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, gMap.getCameraPosition().zoom)); // 줌 레벨 조절 가능
                // 해당 위치에 마커 추가
//                gMap.addMarker(new MarkerOptions().position(location).title(locationName));
            } else {
                // 검색된 결과가 없는 경우 처리
                Toast.makeText(this, "검색된 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 처리
            Toast.makeText(this, "검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastKnownLocationAndMoveCamera()
    {
        // 위치 권한 확인
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
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
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        // 마커 위치 업데이트
                        updateMarker(currentLocation);
                        // 루트에 해당되는 마커들 위치
                        marker_Load(mRootMenu.getItem(0).getTitle().toString());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST_LOCATION_PERMISSION )
        {
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                // 위치 권한이 승인된 경우
                getLastKnownLocationAndMoveCamera();
            } else
            {
                // 위치 권한이 거부된 경우
                Toast.makeText(this, "위치 권한이 필요합니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish(); // 앱 종료
            }
        }
    }

    private void updateMarker(LatLng currentLocation)
    {
        if ( gMap != null )
        {
//            // 기존 마커 제거
//            if ( currentMarker != null )
//                currentMarker.remove();
//
//            // 새로운 마커 추가
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(currentLocation);
//            markerOptions.title("나");
//            markerOptions.snippet("현재위치");
//            currentMarker = gMap.addMarker(markerOptions);

            gMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map)
    {
        gMap = map;
        getLastKnownLocationAndMoveCamera();
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
        mRootMenu = menu;

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

    private void showRootsDialog()
    {
        String rootListString = sharedPreferences.getString(ROOT_NAME, "");
        if ( rootListString.isEmpty() )
        {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        RootDialog rootDialog = new RootDialog(this, screenHeight);

        ArrayList<String> rootList = new ArrayList<>();

        try
        {
            // JSON 문자열을 JSONArray로 파싱하여 각 항목을 ArrayList에 추가
            JSONArray jsonArray = new JSONArray(rootListString);
            for ( int i = 0; i < jsonArray.length(); i++ )
            {
                String rootName = jsonArray.getString(i);
                rootList.add(rootName);
            }
        } catch ( JSONException e )
        {
            e.printStackTrace();
        }
        rootDialog.showDialog(rootList);
        rootDialog.setListener(new RootDialog.DialogListener()
        {
            @Override
            public void onItemClick(String selectedItem)
            {
                /// 클릭된 루트 setTitle로 텍스트 변경, 루트값으로 마커들 업데이트
                mRootMenu.getItem(0).setTitle(selectedItem);
                currentRoot = selectedItem;
                gMapMarkerClear();
                marker_Load(selectedItem);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }

            @Override
            public void onItemLongClick(String selectedItem)
            {

            }
        });
    }

    // 사용자에게 루트메뉴 이름 입력받기
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
        String rootListString = sharedPreferences.getString(ROOT_NAME, "");

        try
        {
            if ( rootListString.isEmpty() )
            {
                JSONArray rootArray = new JSONArray();
                rootArray.put(rootName);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ROOT_NAME, rootArray.toString());
                editor.apply();

                mRootMenu.getItem(0).setTitle(rootName);
                currentRoot = rootName;
            } else
            {
                JSONArray rootArray = new JSONArray(rootListString);

                boolean isDuplicate = false;
                for ( int i = 0; i < rootArray.length(); i++ )
                {
                    String existingRoot = rootArray.getString(i);
                    if ( existingRoot.equals(rootName) )
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("중복된 루트!");
                        builder.setMessage("이미 등록된 루트 입니다.");
                        builder.show();
                        isDuplicate = true;
                        break;
                    }
                }

                if ( ! isDuplicate )
                {
                    rootArray.put(rootName);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ROOT_NAME, rootArray.toString());
                    editor.apply();

                    mRootMenu.getItem(0).setTitle(rootName);
                    currentRoot = rootName;

                    // 기존 마커들 다 삭제
                    gMapMarkerClear();

                    /// TODO 루트에 해당하는 마커들 그리기
                }
            }
        } catch ( Exception e )
        {

        }

    }

    private void gMapMarkerClear()
    {
        for ( Marker marker : markerList )
        {
            marker.remove();
        }
        markerList.clear();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng)
    {
        String root = sharedPreferences.getString(ROOT_NAME, "");
        if ( root.isEmpty() )
        {
            Toast.makeText(this, "현재 설정된 루트가 없습니다. 먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, gMap.getCameraPosition().zoom));

        showDialogToAddMarker(latLng);
    }

    private void showDialogToAddMarker(final LatLng latLng)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("마커 추가");

        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.marker_add_dialog, null);
        EditText markerTitle = ( EditText ) dialogView.findViewById(R.id.marker_title);
        EditText markerDescription = ( EditText ) dialogView.findViewById(R.id.marker_decription);

        builder.setView(dialogView);

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String title = markerTitle.getText().toString();
                String description = markerDescription.getText().toString();
                if(title.isEmpty()){
                    Toast.makeText(MainActivity.this, "장소를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (description.isEmpty()){
                    Toast.makeText(MainActivity.this, "설명을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                addMarkerToMap(latLng, title, description);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });

        builder.show();
    }

    private void addMarkerToMap(LatLng latLng, String markerTitle, String markerDescription)
    {
        String rootListString = sharedPreferences.getString(ROOT_NAME, "");
        if ( rootListString.isEmpty() )
        {
            Toast.makeText(this, "먼저 루트를 추가해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( mRootMenu.getItem(0).getTitle().toString().equals("") )
        {
            Toast.makeText(this, "먼저 루트를 선택해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.title(markerTitle);
        options.position(latLng);
        options.snippet(markerDescription);

        // 맵에 마커 추가하고 List에 마커 추가하기
        Marker marker = gMap.addMarker(options);
        markerList.add(marker);
        marker.showInfoWindow();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude + 0.0001, latLng.longitude + 0.0001), gMap.getCameraPosition().zoom));

        String markerData = markerDescription + "|" + latLng;

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
//            JSONArray _markerDatas;
//            if ( markersInRoot.isEmpty() )
//            {
//                _markerDatas = new JSONArray();
//            } else
//            {
//                _markerDatas = new JSONArray(markersInRoot);
//            }
//
//            _markerDatas.put(markerTitle);
//            editor.putString(root, _markerDatas.toString());
//            editor.apply();
        } catch ( Exception e )
        {

        }
    }

    // 루트에 저장된 마커이름을 통해 마커 생성하고 넣기
    private void marker_Load(String rootName)
    {
        String markerInRoot = sharedPreferences.getString(rootName, "");

        if ( markerInRoot.isEmpty() )
            return;

        String[] markerTitles = markerInRoot.split("\\|");

        if ( markerTitles.length == 0 )
            return;

        for ( String markerTitle : markerTitles )
        {
            if ( sharedPreferences.getString(markerTitle, "").isEmpty() )
            {
                continue;
            }

            String markerDatas = sharedPreferences.getString(markerTitle, "");
            String[] markerData = markerDatas.split("\\|");

            int startIndex = markerData[1].indexOf("(") + 1;
            int endIndex = markerData[1].indexOf(")");
            String latLngStr = markerData[1].substring(startIndex, endIndex);

            String[] latLngParts = latLngStr.split(",");
            double latitude = Double.parseDouble(latLngParts[0].trim());
            double longitude = Double.parseDouble(latLngParts[1].trim());

            MarkerOptions marker = new MarkerOptions()
                    .title(markerTitle)
                    .snippet(markerData[0])
                    .position(new LatLng(latitude, longitude));

            Marker _marker = gMap.addMarker(marker);
            markerList.add(_marker);
            _marker.showInfoWindow();
        }
    }

    private void showMarkerDeleteDialog(final Marker marker)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("마커를 삭제하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 사용자가 확인을 선택하면 마커를 삭제

                String markerTitle = marker.getTitle();
                String markerInRoot = sharedPreferences.getString(currentRoot, "");

                if ( ! markerInRoot.isEmpty() )
                {
                    String[] markerArray = markerInRoot.split("\\|");

                    for ( int i = 0; i < markerArray.length; i++ )
                    {
                        if ( markerArray[i].equals(markerTitle) )
                        {
                            // 마커 리스트에서 마커 삭제
                            Iterator<Marker> iterator = markerList.iterator();
                            while ( iterator.hasNext() )
                            {
                                Marker deleteMarker = iterator.next();
                                if ( deleteMarker.getTitle() != null && deleteMarker.getTitle().equals(markerTitle) )
                                {
                                    iterator.remove();
                                    deleteMarker.remove();
                                    break;
                                }
                            }
                            String afterMarkerInRoot = "";

                            for ( int j = 0; j < markerArray.length; j++ )
                            {
                                if ( i != j )
                                {
                                    afterMarkerInRoot = afterMarkerInRoot + markerArray[j] + "|";
                                }
                            }
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString(currentRoot, afterMarkerInRoot);
                            edit.apply();

                            break;
                        }
                    }
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 사용자가 취소를 선택하면 다이얼로그를 닫음
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showMarkerUpdateDialog(final Marker marker)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("마커 수정");

        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.marker_update_dialog, null);

        builder.setView(dialogView);

        builder.setPositiveButton("수정", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                EditText updateMarkerT = ( EditText ) dialogView.findViewById(R.id.marker_update_title);
                String updateMarkerTitle = updateMarkerT.getText().toString();
                EditText updateMarkerD = ( EditText ) dialogView.findViewById(R.id.marker_update_description);
                String updateMarkerDescription = updateMarkerD.getText().toString();

                // 1. 해당 마커 타이틀을 변수에 저장
                String beforeMarkerTitle = marker.getTitle();

                if ( updateMarkerTitle.isEmpty() ){
                    Toast.makeText(MainActivity.this, "장소를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (updateMarkerDescription.isEmpty()){
                    Toast.makeText(MainActivity.this, "설명을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. 변수에 저장된 마커 타이틀을 이용해 현재 markerList의 marker 찾기
                // 3. 찾은 마커의 타이틀과 설명을 입력 받은값으로 변경
                Iterator<Marker> iterator = markerList.iterator();
                while ( iterator.hasNext() )
                {
                    Marker updateMarker = iterator.next();
                    if ( updateMarker.getTitle() != null && updateMarker.getTitle().equals(beforeMarkerTitle) )
                    {
                        updateMarker.setTitle(updateMarkerTitle);
                        updateMarker.setSnippet(updateMarkerDescription);
                        break;
                    }
                }

                // 4. sharedPreferences에 현재 루트의 값을 변수에 저장된 타이틀로 해당 타이틀 찾기
                // 5. 찾은 타이틀을 지우고 그 자리에 변경된 타이틀 넣기
                String beforeMarker = sharedPreferences.getString(currentRoot, "");
                String[] beforeMarkerArray = beforeMarker.split("\\|");
                String afterMarker = "";

                for ( int i = 0; i < beforeMarkerArray.length; i++ )
                {
                    if ( beforeMarkerArray[i].equals(beforeMarkerTitle) )
                    {
//                        beforeMarkerArray[i] = updateMarkerTitle;
                        afterMarker = afterMarker + "|" + updateMarkerTitle;
                    } else
                    {
                        afterMarker = afterMarker + "|" + beforeMarkerArray[i];
                    }

                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(currentRoot, afterMarker);

                // 6. 변수에 저장된 타이틀을 이용해서 sharedPreferences에 마커타이틀에 해당하는 값들을 찾기
                // 7. 찾은뒤 삭제후 입력받은 타이틀과 설명 그리고 현재 마커의 위치로 다시 insert
                editor.remove(beforeMarkerTitle);
                editor.putString(updateMarkerTitle, updateMarkerDescription + "|" + marker.getPosition().toString());
                editor.apply();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }
}