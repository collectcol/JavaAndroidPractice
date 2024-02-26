package com.example.intentlifecycleandlogcatcheck_ex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings( "deprecation" )
public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("액티비티 테스트 예제");
        android.util.Log.i("액티비티 테스트", "onCreate()");

        // LogCat을 이용한 활용과 Activity LifeCycle Check
        // android.util.Log.d -> Debugging: 디버깅용도
        // android.util.Log.e -> Error: 가장 심각한 오류 발생 시 남기는 로그
        // android.util.Log.i -> Information: 정보를 남기기 위한 로그
        // android.util.Log.v -> Verbose: 상세한 기록을 남기기 위한 로그
        // android.util.Log.w -> Warning: 경고 수준을 남기기 위한 로그
        LogCatCheckingButton();

        // 계산기능을 이용한 양방향 액티비티
        Calculator();

        // 암시적 인텐트
        ImplicitIntent();
    }

    private void Calculator()
    {
        Button btnNewActivity = ( Button ) findViewById(R.id.btnNewActivity);
        btnNewActivity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText edtNum1 = ( EditText ) findViewById(R.id.edtNum1);
                EditText edtNum2 = ( EditText ) findViewById(R.id.edtNum2);
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("Num1", Integer.parseInt(edtNum1.getText().toString()));
                intent.putExtra("Num2", Integer.parseInt(edtNum2.getText().toString()));
                startActivityForResult(intent, 0);
            }
        });
    }

    private void ImplicitIntent()
    {
        Button btnDial1 = (Button) findViewById(R.id.btnDial1);
        Button btnWeb = (Button) findViewById(R.id.btnWeb);
        Button btnGoogle = (Button) findViewById(R.id.btnGoogle);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        Button btnSms = (Button) findViewById(R.id.btnSms);
        Button btnPhoto = (Button) findViewById(R.id.btnPhoto);

        btnDial1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uri uri = Uri.parse("tel:01039374885");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        btnWeb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uri uri = Uri.parse("https://www.naver.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uri uri = Uri.parse("http://maps.google.co.kr/maps?q=" +
                        37.559133 + "," + 126.927824 + "&z" + 15);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, "안드로이드");
                startActivity(intent);
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra("sms_body", "안녕하세요");
                intent.setData(Uri.parse("smsto:" + Uri.encode("010-3937-4885")));
                startActivity(intent);
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK )
        {
            int hap = data.getIntExtra("Hap", 0);
            Toast.makeText(getApplicationContext(), "합계 :" + hap, Toast.LENGTH_SHORT).show();
        }
    }

    private void LogCatCheckingButton()
    {
        Button btnDial = ( Button ) findViewById(R.id.btnDial);
        btnDial.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uri uri = Uri.parse("tel:01039374885");
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        Button btnFinish = ( Button ) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        android.util.Log.i("액티비티 테스트", "onDestroy()");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        android.util.Log.i("액티비티 테스트", "onPause()");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        android.util.Log.i("액티비티 테스트", "onRestart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        android.util.Log.i("액티비티 테스트", "onResume");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        android.util.Log.i("액티비티 테스트", "onStart");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        android.util.Log.i("액티비티 테스트", "onStop()");
    }
}