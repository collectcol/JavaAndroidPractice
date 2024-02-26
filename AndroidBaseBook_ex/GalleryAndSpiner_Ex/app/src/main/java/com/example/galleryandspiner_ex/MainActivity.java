package com.example.galleryandspiner_ex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings( "deprecation" )
public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("개럴리 영화 포스터");

        Gallery gallery = ( Gallery ) findViewById(R.id.gallery);
        MyGalleryAdapter galAdapter = new MyGalleryAdapter(this);
        gallery.setAdapter(galAdapter);

        // 스피너 테스트
        SpinnerEx_Go();
    }

    public class MyGalleryAdapter extends BaseAdapter
    {
        Context context;
        Integer[] posterID = { R.drawable.mov01, R.drawable.mov01, R.drawable.mov03, R.drawable.mov04,
                R.drawable.mov05, R.drawable.mov06, R.drawable.mov07,
                R.drawable.mov08, R.drawable.mov09, R.drawable.mov10 };

        public MyGalleryAdapter(Context c)
        {
            context = c;
        }

        @Override
        public int getCount()
        {
            return posterID.length;
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageview = new ImageView(context);
            imageview.setLayoutParams(new Gallery.LayoutParams(200, 300));
            imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageview.setPadding(5, 5, 5, 5);

            imageview.setImageResource(posterID[position]);

            final int pos = position;
            imageview.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    ImageView ivpPoster = (ImageView) findViewById(R.id.ivPoster);
                    ivpPoster.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ivpPoster.setImageResource(posterID[pos]);
                    return false;
                }
            });

            return imageview;
        }
    }

    private void SpinnerEx_Go(){
        Button btnSpinner = (Button)findViewById(R.id.btnSpinner);
        btnSpinner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), Spinner_Ex.class);
                startActivity(intent);
            }
        });
    }
}