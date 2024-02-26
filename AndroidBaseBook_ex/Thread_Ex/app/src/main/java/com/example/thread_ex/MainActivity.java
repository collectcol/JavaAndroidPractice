package com.example.thread_ex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BasicProgressbarUsingEX();
        ThreadEX();
        UIThreadEX();
    }

    private void UIThreadEX(){
        TextView tv3, tv4;
        Button btn2;
        ProgressBar pb3, pb4;

        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        btn2 = (Button)findViewById(R.id.button2);
        pb3 = (ProgressBar )findViewById(R.id.pb3);
        pb4 = (ProgressBar )findViewById(R.id.pb4);

        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Thread(){
                    public void run() {
                        for (int i = pb3.getProgress(); i < 100; i = i + 2){
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    pb3.setProgress(pb3.getProgress() + 2);
                                    tv3.setText("1번 진행률 : " + pb3.getProgress() + "%");
                                }
                            });
                            SystemClock.sleep(100);
                        }
                    }
                }.start();

                new Thread(){
                    public void run(){
                        for (int i = pb4.getProgress(); i < 100; i = i + 1){
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    pb4.setProgress(pb4.getProgress() + 1);
                                    tv4.setText("2번 진행률 : " + pb4.getProgress() + "%");
                                }
                            });
                            SystemClock.sleep(100);
                        }
                    }
                }.start();
            }
        });
    }
    private void ThreadEX()
    {
        ProgressBar pb1, pb2;
        Button btn;

        pb1 = ( ProgressBar ) findViewById(R.id.pb1);
        pb2 = ( ProgressBar ) findViewById(R.id.pb2);
        btn = ( Button ) findViewById(R.id.button1);

        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Thread()
                {
                    public void run()
                    {
                        for ( int i = pb1.getProgress(); i < 100; i = 1 + 2 )
                        {
                            pb1.setProgress(pb1.getProgress() + 2);
                            SystemClock.sleep(100);
                        }
                    }
                }.start();

                new Thread(){
                    public void run() {
                        for ( int i = pb2.getProgress(); i < 100; i++){
                            pb2.setProgress(pb2.getProgress() + 1);
                            SystemClock.sleep(100);
                        }
                    }
                }.start();
            }
        });
    }

    private void BasicProgressbarUsingEX()
    {
        final ProgressBar pb1;
        Button btnInc, btnDec;
        pb1 = ( ProgressBar ) findViewById(R.id.progressBar1);
        btnInc = ( Button ) findViewById(R.id.btnInc);
        btnDec = ( Button ) findViewById(R.id.btnDec);

        btnInc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pb1.incrementProgressBy(10);
            }
        });

        btnDec.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pb1.incrementProgressBy(- 10);
            }
        });

        final TextView tvSeek = ( TextView ) findViewById(R.id.tvSeek);
        SeekBar seekBar1 = ( SeekBar ) findViewById(R.id.seekBar1);

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tvSeek.setText("진행률 : " + progress + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }
}