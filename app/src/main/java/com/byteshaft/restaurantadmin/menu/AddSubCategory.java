package com.byteshaft.restaurantadmin.menu;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.restaurantadmin.R;
import com.byteshaft.restaurantadmin.utils.AppGlobals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by s9iper1 on 5/25/17.
 */

public class AddSubCategory extends AppCompatActivity {

    private Spinner mSpinner;
    private JSONArray jsonArray;
    private Adapter adapter;
    private EditText nameEditText;
    private EditText weightEditText;
    private EditText priceEdittext;
    private AppCompatButton button;
    private int selectedMenuId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.add_menu_item);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        nameEditText = (EditText) findViewById(R.id.menu_item_name);
        weightEditText = (EditText) findViewById(R.id.menu_item_weight);
        priceEdittext = (EditText) findViewById(R.id.menu_item_price);
        button = (AppCompatButton) findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().trim().isEmpty() ||
                        weightEditText.getText().toString().trim().isEmpty() ||
                        priceEdittext.getText().toString().trim().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "All fields are required",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }


            }
        });
        String category = getIntent().getStringExtra("category");
        try {
             jsonArray = new JSONArray(category);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new Adapter(jsonArray);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    selectedMenuId = jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getSubMenu() {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.i("TAG", request.getResponseURL());
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("TAG", "SUB" + request.getResponseText());
                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {
                
            }
        });
        request.open("GET", String.format("%srestaurants/%s/menu/", AppGlobals.BASE_URL,
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_ID)));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return false;
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
                view = getLayoutInflater().inflate(R.layout.delegate_spinner, viewGroup, false);
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
}
