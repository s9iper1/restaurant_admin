package com.byteshaft.restaurantadmin.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.restaurantadmin.R;
import com.byteshaft.restaurantadmin.utils.AppGlobals;
import com.byteshaft.restaurantadmin.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;


public class MenuMain extends Fragment implements HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private View mBaseView;
    private ListView mListView;
    private Spinner mSpinner;
    private Adapter adapter;
    private JSONArray spinnerArray;
    public static HashMap<Integer, JSONArray> data;
    private ListViewAdapter listViewAdapter;
    private int selectedCategoryId = -1;
    public static boolean sAddedNew = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        data = new HashMap<>();
        setHasOptionsMenu(true);
        getCategories();
        mBaseView = inflater.inflate(R.layout.layout_menu_main, container, false);
        mSpinner = (Spinner) mBaseView.findViewById(R.id.spinner);
        mListView = (ListView) mBaseView.findViewById(R.id.main_list);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jsonObject = spinnerArray.getJSONObject(i);
                    Log.i("TAG", spinnerArray.get(i).toString());
                    selectedCategoryId = jsonObject.getInt("id");
                    if (data.size() > 0) {
                        listViewAdapter = new ListViewAdapter(getActivity().getApplicationContext(),
                                data);
                        mListView.setAdapter(listViewAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sAddedNew) {
            Log.i("TAG", "onresume");
            listViewAdapter.notifyDataSetChanged();
            sAddedNew = false;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_sub_menu:
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        AddSubCategory.class);
                intent.putExtra("category", spinnerArray.toString());
                startActivity(intent);
                return true;
            default:return  false;
        }
    }

    private void getCategories() {
        HttpRequest request = new HttpRequest(getActivity().getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", "http://46.101.15.119:8000/api/restaurant/menu_categories");
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        Log.i("TAG", request.getResponseText());
                        try {
                            spinnerArray = new JSONArray(request.getResponseText());
                            adapter = new Adapter(spinnerArray);
                            mSpinner.setAdapter(adapter);
                            getSubMenu();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

    }

    private void getSubMenu() {
        HttpRequest request = new HttpRequest(getActivity().getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.i("TAG", request.getResponseURL());
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("TAG", "SUB" + request.getResponseText());
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        data.put(jsonObject1.getInt("id"), jsonObject1.getJSONArray("items"));
                                    }
                                    listViewAdapter = new ListViewAdapter(getActivity().getApplicationContext(),
                                            data);
                                    mListView.setAdapter(listViewAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.setOnErrorListener(this);
        request.open("GET", String.format("%srestaurants/%s/menu/", AppGlobals.BASE_URL,
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID)));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
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

    private class Adapter extends BaseAdapter {

        private JSONArray categories;

        private Adapter(JSONArray categories) {
            this.categories = categories;
        }

        private Holder holder;

        @Override
        public int getCount() {
            return categories.length();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, android.view.View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.delegate_spinner, viewGroup, false);
                holder = new Holder();
                holder.name = (TextView) view.findViewById(R.id.category_name);
                view.setTag(holder);
            } else {
                view.getTag();
            }
            try {
                JSONObject jsonObject1 = categories.getJSONObject(i);
                holder.name.setText(jsonObject1.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }
    }
    private class Holder {
        TextView name;
    }

    private class ListViewAdapter extends ArrayAdapter<HashMap<Integer, JSONArray>> {

        private ViewHolder viewHolder;
        private JSONArray jsonArray;

        public ListViewAdapter(Context context, HashMap<Integer, JSONArray> jsonArrayHashMap) {
            super(context, R.layout.delegate_sub_menu);
            Log.i("TAG", "selected" + selectedCategoryId);
            Log.i("TAG", "selected" + jsonArrayHashMap.get(selectedCategoryId));
            jsonArray = jsonArrayHashMap.get(selectedCategoryId);
            if (jsonArray.length() < 1) {
                Snackbar.make(MenuMain.this.getView(), "No menu in this category",
                        Snackbar.LENGTH_SHORT).show();
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable android.view.View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_sub_menu,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.menu_item_name);
                viewHolder.weight = (TextView) convertView.findViewById(R.id.menu_item_weight);
                viewHolder.price = (TextView) convertView.findViewById(R.id.menu_item_price);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                viewHolder.name.setText(jsonObject.getString("name"));
                viewHolder.price.setText(jsonObject.getString("price"));
                viewHolder.weight.setText(jsonObject.getString("weight"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }
    }

    private class ViewHolder {
        TextView name;
        TextView weight;
        TextView price;

    }
}
