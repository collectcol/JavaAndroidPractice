package com.example.googlemaps_ex2.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
        View dialogView = inflater.inflate(R.layout.roots_dialog_button, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.list_textview);
        RootListAdapter adapter = new RootListAdapter(context, dataList);
        listView.setAdapter(adapter);

        dialog = builder.create();
        dialog.show();
    }

    public void setListener(DialogListener listener)
    {
        this.listener = listener;
    }

    public interface DialogListener
    {
        void onTextViewClick(String selectedItem);

        void onButtonUpdate(String selectedItem);

        void onButtonDelete(String selectedItem);
    }

    public void dismissDialog()
    {
        if ( dialog != null && dialog.isShowing() )
        {
            dialog.dismiss();
        }
    }

    private class RootListAdapter extends ArrayAdapter<String> implements View.OnClickListener
    {
        private Context mContext;
        private ArrayList<String> mDataList;

        public RootListAdapter(Context context, ArrayList<String> dataList)
        {
            super(context, R.layout.roots_dialog);
            this.mContext = context;
            this.mDataList = dataList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            View rootView = convertView;
            if ( rootView == null )
            {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rootView = inflater.inflate(R.layout.roots_dialog_button, parent, false);
            }

            String item = mDataList.get(position);

            TextView textView = rootView.findViewById(R.id.list_textview);
            Button updateButton = rootView.findViewById(R.id.btn_update);
            Button deleteButton = rootView.findViewById(R.id.btn_delete);

            textView.setText(item);

            updateButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            textView.setTag(position);
            updateButton.setTag(position);
            deleteButton.setText(position);

            return rootView;
        }

        @Override
        public void onClick(View v)
        {
            int position = ( int ) v.getTag();
            String selectedItem = mDataList.get(position);

            if ( listener != null )
            {
                if(v.getId() == R.id.list_textview){
                    listener.onTextViewClick(selectedItem);
                }
                else if ( v.getId() == R.id.btn_update )
                {
                    listener.onButtonUpdate(selectedItem);
                } else if ( v.getId() == R.id.btn_delete )
                {
                    listener.onButtonDelete(selectedItem);
                }
            }
        }
    }
}
