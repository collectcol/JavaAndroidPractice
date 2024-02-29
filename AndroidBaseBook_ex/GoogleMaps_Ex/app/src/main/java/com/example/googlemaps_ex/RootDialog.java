package com.example.googlemaps_ex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RootDialog
{
    private Context context;
    private int mDialogHeight;
    private ArrayList<String> dataList;
    private DialogListener listener;

    public RootDialog(Context context, int height)
    {
        this.context = context;
        this.mDialogHeight = height;
    }

    public void showDialog(ArrayList<String> dataList)
    {
        this.dataList = dataList;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("등록된 루트");

        LayoutInflater inflater = LayoutInflater.from(context);

        View dialogView = inflater.inflate(R.layout.roots_dialog, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.rootList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

//        int totalHeight = 0;
//        for (int i = 0; i < adapter.getCount(); i++){
//            View listItem = adapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
        DisplayMetrics displayMetrics = new DisplayMetrics();

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = Math.round(mDialogHeight / 4);
        listView.setLayoutParams(params);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = ( String ) parent.getItemAtPosition(position);
                if ( listener != null )
                {
                    listener.onItemClick(selectedItem);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setListener(DialogListener listener)
    {
        this.listener = listener;
    }

    public interface DialogListener
    {
        void onItemClick(String selectedItem);
    }
}
