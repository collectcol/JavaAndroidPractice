package com.example.googlemaps_ex2.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemaps_ex2.R;

import java.util.ArrayList;


public class RootDialog extends AppCompatActivity
{
    private Context context;
    private int mDialogHeight;
    private ArrayList<String> dataList;
    private DialogListener listener;
    private AlertDialog dialog;
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
                    dismissDialog();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = (String) parent.getItemAtPosition(position);
                // 롱클릭 이벤트 처리
                if (listener != null) {
                    listener.onItemLongClick(selectedItem);
                }
                return false;
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    public void setListener(DialogListener listener)
    {
        this.listener = listener;
    }

    public interface DialogListener
    {
        void onItemClick(String selectedItem);
        void onItemLongClick(String selectedItem);
    }

    public void dismissDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
