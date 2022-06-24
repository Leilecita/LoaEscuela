package com.example.loaescuela.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.loaescuela.R;

import java.util.List;

public class SpinnerAdapter  extends ArrayAdapter {

    List<String> Text;
    Context mContext;
    String type;

    public SpinnerAdapter(Context context, int resource, List<String> text) {
        super(context, resource, text);
        Text = text;
        mContext = context;
        type= "todos";
    }

    public void setType(String type){
        this.type = type;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        /*if(type.equals("todos")){
            view = inflater.inflate(R.layout.spinner_item_worker, parent, false);
        }else if(type.equals("create")){
            view = inflater.inflate(R.layout.item_custom_create, parent, false);
        }else{
            view = inflater.inflate(R.layout.item_custom_edith_order, parent, false);
        }*/
        view = inflater.inflate(R.layout.item_custom, parent, false);


        TextView tv = (TextView)view.findViewById(R.id.textView1);

        tv.setText(Text.get(position));

        return view;
    }

    @Override
    public View getDropDownView(int position,View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View view;
        /*if(type.equals("todos")){
            view = inflater.inflate(R.layout.item_custom, parent, false);
        }else if(type.equals("create")){
            view = inflater.inflate(R.layout.item_custom_create, parent, false);
        }else{
            view = inflater.inflate(R.layout.item_custom, parent, false);
        }

         */

        view = inflater.inflate(R.layout.item_custom, parent, false);

        TextView tv = (TextView)view.findViewById(R.id.textView1);

        tv.setText(Text.get(position));
        // return getCustomView(position, convertView, parent);
        return view;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}




