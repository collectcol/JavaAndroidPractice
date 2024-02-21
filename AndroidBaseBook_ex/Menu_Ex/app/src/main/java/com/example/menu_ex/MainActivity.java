package com.example.menu_ex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity
{
    LinearLayout baseLayout;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("배경색 바꾸기");
        getSupportActionBar().show();
        baseLayout = ( LinearLayout ) findViewById(R.id.baseLayout);
        button1 = ( Button ) findViewById(R.id.button1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int iId = item.getItemId();

        // xml 사용해서 메뉴 추가했을시
//        if ( iId == R.id.itemRed )
//        {
//            baseLayout.setBackgroundColor(Color.RED);
//            return true;
//        } else if ( iId == R.id.itemGreen )
//        {
//            baseLayout.setBackgroundColor(Color.GREEN);
//            return true;
//        } else if ( iId == R.id.itemBlue )
//        {
//            baseLayout.setBackgroundColor(Color.BLUE);
//            return true;
//        } else if ( iId == R.id.subRotate )
//        {
//            button1.setRotation(45);
//            return true;
//        } else if ( iId == R.id.subSize )
//        {
//            button1.setScaleX(2);
//            return true;
//        }

        // Java 코드 사용해서 메뉴 추가했을시
        if ( iId == 1 )
        {
            baseLayout.setBackgroundColor(Color.RED);
            return true;
        } else if ( iId == 2 )
        {
            baseLayout.setBackgroundColor(Color.GREEN);
            return true;
        } else if ( iId == 3 )
        {
            baseLayout.setBackgroundColor(Color.BLUE);
            return true;
        } else if ( iId == 4 )
        {
            button1.setRotation(45);
            return true;
        } else if ( iId == 5 )
        {
            button1.setScaleX(2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        // xml 파일로 메뉴 추가할때
//        MenuInflater mInflater = getMenuInflater();
//        mInflater.inflate(R.menu.menu1, menu);

        // Java코드로 추가할때
        menu.add(0, 1, 0, "배경색(빨강)");
        menu.add(0, 2, 0, "배경색(초록)");
        menu.add(0, 3, 0, "배경색(파랑)");

        SubMenu sMenu = menu.addSubMenu("버튼 병경 >>");
        sMenu.add(0, 4, 0, "버튼 45도 회전");
        sMenu.add(0, 5, 0, "버튼 2배 확대");

        return true;
    }
}