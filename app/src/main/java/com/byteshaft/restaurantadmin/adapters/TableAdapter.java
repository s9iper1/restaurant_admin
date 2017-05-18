package com.byteshaft.restaurantadmin.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.restaurantadmin.R;
import com.byteshaft.restaurantadmin.gettersetter.AddTable;

import java.util.ArrayList;



public class TableAdapter extends BaseAdapter {

    private ViewHolder viewHolder;
    private ArrayList<AddTable> addTables;
    private Activity activity;

    public TableAdapter(Activity activity, ArrayList<AddTable> addTables) {
        this.activity = activity;
        this.addTables = addTables;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.delegate_tables, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tableNumber = (TextView) convertView.findViewById(R.id.table_number_text_view);
            viewHolder.tableStatus = (TextView) convertView.findViewById(R.id.status_text_view);
            viewHolder.tableImage = (ImageView) convertView.findViewById(R.id.table_image_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AddTable addTable = addTables.get(position);
        viewHolder.tableNumber.setText(addTable.getTableNumber());
        viewHolder.tableStatus.setText(addTable.getTableStatus());
        viewHolder.tableImage.setImageResource(R.mipmap.main_table);
        return convertView;
    }

    @Override
    public int getCount() {
        return addTables.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView tableNumber;
        TextView tableStatus;
        ImageView tableImage;
    }
}
