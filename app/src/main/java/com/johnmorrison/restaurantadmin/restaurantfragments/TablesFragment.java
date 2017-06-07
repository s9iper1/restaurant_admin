package com.johnmorrison.restaurantadmin.restaurantfragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.johnmorrison.restaurantadmin.MainActivity;
import com.byteshaft.restaurantadmin.R;
import com.johnmorrison.restaurantadmin.TableDetails;
import com.johnmorrison.restaurantadmin.gettersetter.OrderDetails;
import com.johnmorrison.restaurantadmin.gettersetter.TableDetail;
import com.johnmorrison.restaurantadmin.utils.AppGlobals;
import com.johnmorrison.restaurantadmin.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by s9iper1 on 5/26/17.
 */

public class TablesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    public ArrayList<TableDetail> tableDetails;
    public ArrayList<Integer> alreadyAddedTableNumber;
    private View mBaseView;
    private GridView tableView;
    private Button mAddTableButton;
    private TableAdapter tableAdapter;
    private int currentArraySize = 0;
    private boolean isLongPress = false;
    private int positionOfDelete = -1;
    public static boolean updated = false;
    private static TablesFragment sInstance;

    public static TablesFragment getInstance() {
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.content_main, container, false);
        sInstance = this;
        tableView = (GridView) mBaseView.findViewById(R.id.table_view);
        mAddTableButton = (Button) mBaseView.findViewById(R.id.add_table_button);
        alreadyAddedTableNumber = new ArrayList<>();
        tableDetails = new ArrayList<>();
        tableView.setOnItemClickListener(this);
        mAddTableButton.setOnClickListener(this);
        getTables();
        tableAdapter = new TableAdapter(tableDetails);
        tableView.setAdapter(tableAdapter);
        tableView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isLongPress) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), TableDetails.class);
                    intent.putExtra("serializer", tableDetails.get(i));
                    intent.putExtra("position", i);
                    startActivity(intent);
                } else {
                    isLongPress = false;
                }
            }
        });
        tableView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("TAG", "item" + tableDetails.get(i).getId());
                isLongPress = true;
                positionOfDelete = i;
                final TableDetail tableDetail = tableDetails.get(i);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Delete");
                alertDialogBuilder.setMessage("Do you want to delete this table?")
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteTable(tableDetail.getId());
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });
        return mBaseView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onResume() {
        super.onResume();
        isLongPress = false;
        if (tableDetails.size() > currentArraySize || updated) {
            tableAdapter.notifyDataSetChanged();
            if (updated) {
                updated = false;
            } else {
                currentArraySize = tableDetails.size();
            }
        }
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), TableDetails.class));
    }

    private void getTables() {
        HttpRequest request = new HttpRequest(getActivity().getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%srestaurant/tables/", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                System.out.println(request.getResponseURL());
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText());
                        try {
                            JSONArray jsonArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                TableDetail tableDetail = new TableDetail();
                                tableDetail.setId(jsonObject.getInt("id"));
                                tableDetail.setRestaurantId(jsonObject.getInt("id"));
                                tableDetail.setServiceAble(jsonObject.getBoolean("serviceable"));
                                tableDetail.setTableNumber(jsonObject.getInt("table_number"));
                                tableDetail.setNumberOfChair(jsonObject.getInt("number_of_chairs"));
                                tableDetail.setMinimumBookingTime(jsonObject.getInt("minimum_booking_time"));
                                tableDetail.setLocationInRestaurant(jsonObject.getString("location"));
                                alreadyAddedTableNumber.add(jsonObject.getInt("table_number"));
                                tableDetails.add(tableDetail);
                                tableAdapter.notifyDataSetChanged();

                                JSONArray bookings = jsonObject.getJSONArray("bookings");
                                Log.i("TAG", "booking " + bookings);
                                for (int j = 0; j < bookings.length(); j++) {
                                    JSONObject booking = bookings.getJSONObject(j);
                                    JSONArray orderArray = booking.getJSONArray("order");
                                    ArrayList<OrderDetails> orderDetailses = new ArrayList<>();
                                    for (int k = 0; k < orderArray.length(); k++) {
                                        JSONObject order = orderArray.getJSONObject(k);
                                        OrderDetails orderDetails = new OrderDetails();
                                        orderDetails.setTableNumber("Table #: "+booking.getInt("table"));
                                        orderDetails.setStartEndTime("start time:"+booking
                                                .getString("start_time") + " end Time: " +
                                                booking.getString("end_time"));
                                        orderDetails.setOrderDetails("Order:"+
                                                order.getString("name") + "(" +order.getDouble("weight")
                                                + ")" + " price:" + order.getDouble("price"));
                                        orderDetailses.add(orderDetails);
                                    }
                                    Log.i("TAG", booking.getString("date"));
                                    MainActivity.sHashMap.put(booking.getString("date"), orderDetailses);
                                }

                            }
                            currentArraySize = tableDetails.size();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        Log.i("TAG", request.getResponseText());
                        Snackbar.make(getView(), "Successfully Deleted",
                                Snackbar.LENGTH_SHORT).show();
                        tableDetails.remove(positionOfDelete);
                        tableAdapter.notifyDataSetChanged();
                        break;
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(getView(), "Connection Timeout");
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                break;
        }
    }

    private void deleteTable(int id) {
        HttpRequest request = new HttpRequest(getActivity().getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("DELETE", String.format("%srestaurant/tables/%s", AppGlobals.BASE_URL, id));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    private class TableAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<TableDetail> addTables;

        public TableAdapter(ArrayList<TableDetail> addTables) {
            this.addTables = addTables;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_tables, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tableNumber = (TextView) convertView.findViewById(R.id.table_number_text_view);
                viewHolder.tableStatus = (TextView) convertView.findViewById(R.id.status_text_view);
                viewHolder.tableImage = (ImageView) convertView.findViewById(R.id.table_image_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TableDetail tableDetails = addTables.get(position);
            viewHolder.tableNumber.setText(String.valueOf(tableDetails.getTableNumber()));
            if (tableDetails.isServiceAble()) {
                viewHolder.tableStatus.setText("Available");
            } else {
                viewHolder.tableStatus.setText("Reserved");
            }
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
}
