package com.example.listviewandgridview_ex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("리스트뷰 테스트");

        final String[] mid = { "히어로즈", "24시", "로스트", "로스트룸", "스몰빌", "탐정몽크",
                "빅뱅이론", "프랜즈", "덱스터", "글리", "가쉽걸", "테이큰", "슈퍼내추럴", "브이" };

        ListView list = ( ListView ) findViewById(R.id.listView1);

        // 일반적인 리스트
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mid);
//        list.setAdapter(adapter);

        // 체크박스형식의 리스트
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mid);
//        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        list.setAdapter(adapter);

        // 라디오버튼 형식의 리스트
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mid);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), mid[position], Toast.LENGTH_SHORT).show();
            }
        });

        ListAdd_Go();
        GridView_Go();
    }

    private void ListAdd_Go()
    {
        Button go = ( Button ) findViewById(R.id.btnAdd);
        go.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), DynamicListView.class);
                startActivity(intent);
            }
        });
    }

    private void GridView_Go()
    {
        Button go = (Button) findViewById(R.id.btnGridView);
        go.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), GridView_Ex.class);
                startActivity(intent);
            }
        });
    }
}