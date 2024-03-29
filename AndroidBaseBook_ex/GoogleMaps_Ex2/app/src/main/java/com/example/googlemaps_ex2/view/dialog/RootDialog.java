package com.example.googlemaps_ex2.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.googlemaps_ex2.R;

import java.util.ArrayList;


public class RootDialog extends BaseAdapter
{
    LayoutInflater inflater = null;
    private ArrayList<String> arrayList = null;
    private int mListCnt = 0;
    private int mDialogHeight;
    private DialogClickListener dialogClickListener;
    private Dialog dialog;

    public interface DialogClickListener
    {
        void onButtonChoice(String selectedItem);

        void onButtonUpdate(String selectedItem);

        void onButtonDelete(String selectedItem);
    }

    public RootDialog(ArrayList<String> data, int dialogHeight, DialogClickListener dialogClickListener, Dialog dialog)
    {
        arrayList = data;
        mListCnt = arrayList.size();
        this.dialogClickListener = dialogClickListener;
        this.dialog = dialog;
    }

    @Override
    public int getCount()
    {
        return mListCnt;
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
        if ( convertView == null )
        {
            final Context context = parent.getContext();
            if ( inflater == null )
            {
                inflater = ( LayoutInflater ) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.roots_dialog_button, parent, false);
        }

        TextView rootName = ( TextView ) convertView.findViewById(R.id.list_textview);
        Button btnChoice = ( Button ) convertView.findViewById(R.id.btn_choice);
        Button btnUpdate = ( Button ) convertView.findViewById(R.id.btn_update);
        Button btnDelete = ( Button ) convertView.findViewById(R.id.btn_delete);

        btnChoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String selectedItem = arrayList.get(position);
                if ( dialogClickListener != null )
                {
                    dialogClickListener.onButtonChoice(selectedItem);
                    dialog.dismiss();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String selectedItem = arrayList.get(position);
                if ( dialogClickListener != null )
                {
                    dialog.dismiss();
                    dialogClickListener.onButtonUpdate(selectedItem);

                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String selectedItem = arrayList.get(position);
                if ( dialogClickListener != null )
                {
                    dialog.dismiss();
                    dialogClickListener.onButtonDelete(selectedItem);
                }
            }
        });
        rootName.setText(arrayList.get(position));
        return convertView;
    }
}
