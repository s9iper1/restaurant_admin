package com.byteshaft.restaurantadmin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.restaurantadmin.gettersetter.TableDetail;
import com.byteshaft.restaurantadmin.restaurantfragments.TablesFragment;
import com.byteshaft.restaurantadmin.utils.AppGlobals;
import com.byteshaft.restaurantadmin.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by husnain on 5/17/17.
 */

public class TableDetails extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private EditText mTableNumber;
    private EditText mMinimumBookingTime;
    private EditText mChair;
    private TextView mTableLocation;

    private String mTableNumberString;
    private String mMinimumBookingTimeString;
    private String mChairString;
    private String mTableLocationString;
    private Button mSaveButton;
    private HttpRequest mRequest;
    private String method = "POST";
    private String url;
    private TableDetail tableDetail;
    private int position = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_table_details);
        mTableNumber = (EditText) findViewById(R.id.table_number_edit_text);
        mMinimumBookingTime = (EditText) findViewById(R.id.minimum_booking_edit_text);
        mChair = (EditText) findViewById(R.id.chairs_edit_text);
        mTableLocation = (EditText) findViewById(R.id.table_location_edit_text);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
        if (getIntent().getExtras() != null) {
            method = "PUT";
            position = getIntent().getIntExtra("position", -1);
            tableDetail = (TableDetail) getIntent().getSerializableExtra("serializer");
            getSupportActionBar().setTitle("Update Table");
            mTableNumber.setText(String.valueOf(tableDetail.getTableNumber()));
            mMinimumBookingTime.setText(String.valueOf(tableDetail.getMinimumBookingTime()));
            mChair.setText(String.valueOf(tableDetail.getNumberOfChair()));
            mTableLocation.setText(String.valueOf(tableDetail.getLocationInRestaurant()));
            mSaveButton.setText("Update");
        } else {
            mTableNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!charSequence.toString().trim().isEmpty()) {
                        int tableNumber = Integer.valueOf(charSequence.toString());
                        if (TablesFragment.getInstance().alreadyAddedTableNumber.contains(tableNumber)) {
                            mTableNumber.setError("Table already exist");
                        } else {
                            mTableNumber.setError(null);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {


                }
            });
        }

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

    @Override
    public void onClick(View v) {
        if (validate()) {
            addTableDetails(mTableNumberString, mChairString, mMinimumBookingTimeString, mTableLocationString);
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(findViewById(R.id.content_main), "connection time out");
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(findViewById(R.id.content_main), exception.getLocalizedMessage());
                break;
        }
    }

    private void addTableDetails(String tableNumber, String numberOfChairs, String minimumBookingTime, String location) {
        mRequest = new HttpRequest(this);
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.setOnErrorListener(this);
        String dialogText = "Adding table";
        if (method.equals("POST")) {
            url = String.format("%srestaurant/tables/", AppGlobals.BASE_URL);
        } else {
            dialogText = "updating table";
            url = String.format("%srestaurant/tables/%s", AppGlobals.BASE_URL, tableDetail.getId());
        }
        mRequest.open(method, url);
        mRequest.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        mRequest.send(getTableData(tableNumber, numberOfChairs, minimumBookingTime, location));
        Helpers.showProgressDialog(TableDetails.this, dialogText);
    }

    private String getTableData(String tableNumber, String numberOfChairs, String minimumBookingTime, String location) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("table_number", tableNumber);
            jsonObject.put("number_of_chairs", numberOfChairs);
            jsonObject.put("minimum_booking_time", minimumBookingTime);
            jsonObject.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                System.out.println(request.getResponseText());
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(TableDetails.this, getString(R.string.table_failed), getString(R.string.check_internet));
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(TableDetails.this, getString(R.string.table_failed), "Fill all Tables data");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        Toast.makeText(TableDetails.this, "Table added successfully", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            TableDetail tableDetail = new TableDetail();
                            tableDetail.setId(jsonObject.getInt("id"));
                            tableDetail.setRestaurantId(jsonObject.getInt("id"));
                            tableDetail.setServiceAble(jsonObject.getBoolean("serviceable"));
                            tableDetail.setTableNumber(jsonObject.getInt("table_number"));
                            tableDetail.setNumberOfChair(jsonObject.getInt("number_of_chairs"));
                            tableDetail.setMinimumBookingTime(jsonObject.getInt("minimum_booking_time"));
                            tableDetail.setLocationInRestaurant(jsonObject.getString("location"));
                            TablesFragment.getInstance().alreadyAddedTableNumber.add(jsonObject.getInt("table_number"));
                            TablesFragment.getInstance().tableDetails.add(tableDetail);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case HttpURLConnection.HTTP_OK:
                        Toast.makeText(TableDetails.this, "Table updated successfully", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            TableDetail tableDetail = new TableDetail();
                            tableDetail.setId(jsonObject.getInt("id"));
                            tableDetail.setRestaurantId(jsonObject.getInt("id"));
                            tableDetail.setServiceAble(jsonObject.getBoolean("serviceable"));
                            tableDetail.setTableNumber(jsonObject.getInt("table_number"));
                            tableDetail.setNumberOfChair(jsonObject.getInt("number_of_chairs"));
                            tableDetail.setMinimumBookingTime(jsonObject.getInt("minimum_booking_time"));
                            tableDetail.setLocationInRestaurant(jsonObject.getString("location"));
                            TablesFragment.getInstance().tableDetails.remove(position);
                            TablesFragment.getInstance().alreadyAddedTableNumber.add(jsonObject.getInt("table_number"));
                            TablesFragment.getInstance().tableDetails.add(tableDetail);
                            TablesFragment.updated = true;
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

    }

    public boolean validate() {
        boolean valid = true;
        mTableNumberString = mTableNumber.getText().toString();
        mMinimumBookingTimeString = mMinimumBookingTime.getText().toString();
        mChairString = mChair.getText().toString();
        mTableLocationString = mTableLocation.getText().toString();
        System.out.println(mChairString + "chairs");
        if (TablesFragment.getInstance().alreadyAddedTableNumber
                .contains(Integer.valueOf(mTableNumberString))) {
            Toast.makeText(this, "This table Number already added", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (mTableNumberString.trim().isEmpty()) {
            mTableNumber.setError("please add table number");
            valid = false;
        } else {
            mTableNumber.setError(null);
        }
        if (mMinimumBookingTimeString.isEmpty()) {
            mMinimumBookingTime.setError("please provide minimum time for table");
            valid = false;
        } else {
            mMinimumBookingTime.setError(null);
        }
        if (mChairString.isEmpty()) {
            mChair.setError("please provide chairs for a table");
            valid = false;
        } else {
            mChair.setError(null);
        }
        if (mTableLocationString.isEmpty()) {
            mTableLocation.setError("please provide table location");
            valid = false;
        } else {
            mTableLocation.setError(null);
        }
        return valid;
    }
}
