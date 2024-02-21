package com.example.innermemoryfilestream_ex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    DatePicker dp;
    EditText edtDiary;
    Button btnWrite;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("간단 일기장 & 파일관련 기능 테스트");

        // 일기쓰기 기능
        DiaryWriting();
        // /res/raq에서 파일 읽기
        res_raq_FileRead();
        // SD카드 읽기
        SDFileRead();
        // SD카드에 디렉터리 설정, 삭제
        SDDirectoryMadeAndDelete();
        // 시스템폴더의 폴더/파일 목록 출력
        SystemFolderFileListWrite();
    }

    private void DiaryWriting()
    {
        dp = ( DatePicker ) findViewById(R.id.datePicker1);
        edtDiary = ( EditText ) findViewById(R.id.edtDiary);
        btnWrite = ( Button ) findViewById(R.id.btnWrite);

        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);

        dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                fileName = Integer.toString(year) + "_" + Integer.toString(monthOfYear + 1) + "_"
                        + Integer.toString(dayOfMonth) + ".txt";
                String str = readDiary(fileName);
                edtDiary.setText(str);
                btnWrite.setEnabled(true);
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    FileOutputStream outFs = openFileOutput(fileName, Context.MODE_PRIVATE);
                    String str = edtDiary.getText().toString();
                    outFs.write(str.getBytes());
                    outFs.close();
                    Toast.makeText(getApplicationContext(), fileName + " 이 저장됨", Toast.LENGTH_SHORT).show();
                } catch ( IOException e )
                {

                }
            }
        });
    }

    private void res_raq_FileRead()
    {
        Button btnRead;
        final EditText edtRaw;
        btnRead = ( Button ) findViewById(R.id.btnRead);
        edtRaw = ( EditText ) findViewById(R.id.edtRaw);

        btnRead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    InputStream inputS = getResources().openRawResource(R.raw.raw_test);
                    byte[] txt = new byte[inputS.available()];
                    inputS.read(txt);
                    edtRaw.setText(new String(txt));
                    inputS.close();
                } catch ( IOException e )
                {

                }
            }
        });
    }

    private void SDFileRead()
    {
        Button btnSDRead;
        final EditText edtSD;
        btnSDRead = ( Button ) findViewById(R.id.btnSDRead);
        edtSD = ( EditText ) findViewById(R.id.edtSD);
        ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, MODE_PRIVATE);

        btnSDRead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File sdCardRoot = Environment.getExternalStorageDirectory();
                try
                {
                    FileInputStream inFs = new FileInputStream(sdCardRoot);
                    byte[] txt = new byte[inFs.available()];
                    inFs.read(txt);
                    edtSD.setText(new String(txt));
                    inFs.close();
                } catch ( FileNotFoundException e )
                {
                    throw new RuntimeException(e);
                } catch ( IOException e )
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void SDDirectoryMadeAndDelete()
    {
        Button btnMkdir, btnRmdir;
        btnMkdir = ( Button ) findViewById(R.id.btnMkdir);
        btnRmdir = ( Button ) findViewById(R.id.btnRmdir);
        final String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File myDir = new File(strSDpath + "/mydir");

        btnMkdir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myDir.mkdir();
            }
        });
        btnRmdir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myDir.delete();
            }
        });
    }

    private void SystemFolderFileListWrite(){
        Button btnFilelist;
        final EditText edtFilelist;
        btnFilelist = (Button)findViewById(R.id.btnFilelist);
        edtFilelist = (EditText ) findViewById(R.id.edtFilelist);

        btnFilelist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sysDir = Environment.getRootDirectory().getAbsolutePath();
                File[] sysFiles = (new File(sysDir).listFiles());

                String strFname;
                for(int i= 0; i < sysFiles.length; i++){
                    if (sysFiles[i].isDirectory() == true)
                        strFname = "<폴더> " + sysFiles[i].toString();
                    else strFname = "<파일> " + sysFiles[i].toString();

                    edtFilelist.setText(edtFilelist.getText() + "\n" + strFname);
                }
            }
        });
    }

    String readDiary(String fName)
    {
        String diaryStr = null;
        FileInputStream inFs;
        try
        {
            inFs = openFileInput(fName);
            byte[] txt = new byte[500];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
            btnWrite.setText("수정하기");
        } catch ( IOException e )
        {
            edtDiary.setHint("일기 없음");
            btnWrite.setText("새로 저장");
        }
        return diaryStr;
    }
}