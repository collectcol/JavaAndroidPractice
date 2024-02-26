package com.example.intent_ex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    int ImageID = 1000;
    int ButtonID = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainSetting();
        setTitle("사진 선호도 투표");
    }



    private void MainSetting()
    {
        // MainActivity의 메인 레이아웃 가져오기
        LinearLayout mainLayout = findViewById(R.id.mainLayout);

        // LinearLayout의 가중치 설정
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                3
        );

        // LinearLayout을 생성하고 설정
        LinearLayout[] baseLayouts = new LinearLayout[3];
        for ( int i = 0; i < 3; i++ )
        {
            baseLayouts[i] = new LinearLayout(this);
            baseLayouts[i].setLayoutParams(layoutParams);
            baseLayouts[i].setOrientation(LinearLayout.HORIZONTAL);
            mainLayout.addView(baseLayouts[i]);
        }

        // 화면 크기에 따라 이미지뷰 크기 동적으로 조정
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int imageViewSize = screenWidth / 3; // 화면 너비의 1/3 크기로 설정

        final int voteCount[] = new int[9];
        for ( int i = 0; i < 9; i++ )
        {
            voteCount[i] = 0;
        }

        int[] imge = new int[]{ R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
                R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8, R.drawable.img9 };

        final String imgName[] = { "해변그림1", "해변그림2", "해변그림3", "해변그림4", "해변그림5",
                "해변그림6", "해변그림7", "해변그림8", "해변그림9" };

        // 이미지뷰를 생성하고 설정
        for ( int i = 0; i < 9; i++ )
        {
            MyImageView myImageView = new MyImageView(this, imge[i], imageViewSize, imageViewSize);
            myImageView.setId(ImageID + i);

            final int index = i;
            myImageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    voteCount[index]++;
                    Toast.makeText(getApplicationContext(), imgName[index] + ": 총 " + voteCount[index] + " 표", Toast.LENGTH_SHORT).show();
                }
            });
            int baseLayoutIndex = i / 3; // 0, 1, 2 중 하나의 baseLayout을 선택
            baseLayouts[baseLayoutIndex].addView(myImageView);
        }

        Button btnResult = new Button(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        );
        btnResult.setLayoutParams(buttonParams);
        btnResult.setId(ButtonID);
        btnResult.setText("투표종료");

        mainLayout.addView(btnResult);
        ResultButton(btnResult, voteCount, imgName);
    }

    private void ResultButton(Button btnResult, int[] voteCount, String[] imgName){
        Button btnFinish = btnResult;
        btnFinish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("VoteCount", voteCount);
                intent.putExtra("ImageName", imgName);
                startActivity((intent));
            }
        });
    }
}

