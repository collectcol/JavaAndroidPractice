package com.example.googlemaps_ex2.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemaps_ex2.R;

public class MarkerAddDialog extends AppCompatActivity
{
    private Context context;
    private DialogListener listener;
    private AlertDialog dialog;

    public MarkerAddDialog(Context context)
    {
        this.context = context;
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("마커 추가");

        LayoutInflater inflater = LayoutInflater.from(context);

        View dialogView = inflater.inflate(R.layout.marker_add_dialog, null);
        EditText markerTitle = ( EditText ) dialogView.findViewById(R.id.marker_title);
        EditText markerSnippet = ( EditText ) dialogView.findViewById(R.id.marker_decription);

        builder.setView(dialogView);
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(listener != null){
                    String title = markerTitle.getText().toString();
                    String snippet = markerSnippet.getText().toString();
                    listener.onPositiveButtonClick(title, snippet);
                    dismissDialog();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(listener != null){
                    listener.onNegativeButtonClick();
                    dismissDialog();
                }
            }
        });
    }
    public void setListener(DialogListener listener)
    {
        this.listener = listener;
    }
    public interface DialogListener
    {
        void onPositiveButtonClick(String title, String snippet);
        void onNegativeButtonClick();
    }

    public void dismissDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
