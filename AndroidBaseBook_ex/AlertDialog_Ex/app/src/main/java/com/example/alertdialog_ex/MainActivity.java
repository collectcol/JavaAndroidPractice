package com.example.alertdialog_ex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonTalkBox = ( Button ) findViewById(R.id.buttonTalkBox);
        buttonTalkBox.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("제목입니다");
                dlg.setMessage("이곳이 내용입니다.");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.show();
            }
        });

        final Button buttonTalkBox2 = ( Button ) findViewById(R.id.buttonTalkBox2);
        buttonTalkBox2.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("제목입니다");
                dlg.setMessage("이곳이 내용입니다.");
                dlg.setIcon(R.mipmap.ic_launcher);

                // 이벤트 없을때는 리스너에 null 넣기
//                dlg.setPositiveButton("확인", null);

                // 이벤트 있을때
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainActivity.this, "확인을 눌렀습니다", Toast.LENGTH_SHORT);
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        // 목록 대화상자
        final Button buttonListTalkBox = ( Button ) findViewById(R.id.buttonListTalkBox);
        buttonListTalkBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String[] versionArray = new String[]{ "파이", "Q(10)", "R(11)" };
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("좋아하는 버전은(목록 대화상자 예시");
                dlg.setIcon(R.mipmap.ic_launcher);
                // 목록을 선택하면 대화상자가 닫힘
                dlg.setItems(versionArray, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        buttonTalkBox.setText(versionArray[which]);
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();
            }
        });

        final Button buttonListTalBox_Radio = ( Button ) findViewById(R.id.buttonListTalBox_RadioButton);
        buttonListTalBox_Radio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String[] versionArray = new String[]{ "파이", "Q(10)", "R(11)" };
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("좋아하는 버전은(목록 대화상자 예시");
                dlg.setIcon(R.mipmap.ic_launcher);
                // 목록을 선택해도 대화상자가 닫히지 않음
                dlg.setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        buttonTalkBox.setText(versionArray[which]);
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();
            }
        });

        final Button buttonListTalBox_CheckBox = ( Button ) findViewById(R.id.buttonListTalBox_CheckBox);
        buttonListTalBox_CheckBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String[] versionArray = new String[]{ "파이", "Q(10)", "R(11)" };
                final boolean[] checkArray = new boolean[]{ true, false, false };
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("좋아하는 버전은(목록 대화상자 예시");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setMultiChoiceItems(versionArray, checkArray, new DialogInterface.OnMultiChoiceClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        buttonListTalkBox.setText(versionArray[which]);
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();
            }
        });
    }
}