package com.example.intent_ex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyImageView extends androidx.appcompat.widget.AppCompatImageView
{
    public MyImageView(@NonNull Context context, int imageResource, int width, int height)
    {
        super(context);
        init(imageResource, width, height);
    }

    public MyImageView(@NonNull Context context, @Nullable AttributeSet attrs, int imageResource, int width, int height)
    {
        super(context, attrs);
        init(imageResource, width, height);
    }

    public MyImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int imageResource, int width, int height)
    {
        super(context, attrs, defStyleAttr);
        init(imageResource, width, height);
    }

    private void init(int imageResource, int width, int height){
        setImageResource(imageResource);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        setLayoutParams(params);
    }
}
