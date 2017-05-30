package com.byteshaft.restaurantadmin.restaurantfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.restaurantadmin.MainActivity;
import com.byteshaft.restaurantadmin.R;
import com.byteshaft.restaurantadmin.gettersetter.OrderDetails;
import com.byteshaft.restaurantadmin.utils.Helpers;

import java.util.ArrayList;

/**
 * Created by s9iper1 on 5/30/17.
 */

public class TodayOrders extends Fragment {

    private View mBaseView;
    private ListView mListView;
    private Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.today_orders, container, false);
        mListView = (ListView) mBaseView.findViewById(R.id.orders);
        Log.i("TAG", "array " + MainActivity.sHashMap
                .get(Helpers.getDate()));
        adapter = new Adapter(getActivity().getApplicationContext(), MainActivity.sHashMap
                .get(Helpers.getDate()));
        mListView.setAdapter(adapter);
        return mBaseView;
    }


    private class Adapter extends ArrayAdapter<String> {

        private ViewHolder viewHolder;
        private ArrayList<OrderDetails> orderDetailsArrayList;

        public Adapter(Context context, ArrayList<OrderDetails> orderDetailsArrayList) {
            super(context, R.layout.order_delegate);
            this.orderDetailsArrayList = orderDetailsArrayList;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.order_delegate, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tableNumber = (TextView) convertView.findViewById(R.id.table_number);
                viewHolder.startEndTime = (TextView) convertView.findViewById(R.id.start_end_time);
                viewHolder.orderDetails = (TextView) convertView.findViewById(R.id.order_details);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OrderDetails orderDetails = orderDetailsArrayList.get(position);
            viewHolder.tableNumber.setText(String.valueOf(orderDetails.getTableNumber()));
            viewHolder.startEndTime.setText(orderDetails.getStartEndTime());
            viewHolder.orderDetails.setText(orderDetails.getOrderDetails());
            return convertView;
        }

        @Override
        public int getCount() {
            return orderDetailsArrayList.size();
        }
    }

    private class ViewHolder {

        TextView tableNumber;
        TextView startEndTime;
        TextView orderDetails;
    }
}
