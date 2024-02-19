package com.MainLoadingTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myButton1 = (Button) findViewById(R.id.btnFirst);
        myButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FirstLogoActivity.class);
                startActivity(intent);
            }
        });

        Button myButton2 = (Button) findViewById(R.id.btnSecond);
        myButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondLogoActivity.class);
                startActivity(intent);
            }
        });
    }


}