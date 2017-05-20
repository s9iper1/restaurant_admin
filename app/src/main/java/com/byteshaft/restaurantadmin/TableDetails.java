package com.byteshaft.restaurantadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.restaurantadmin.utils.AppGlobals;
import com.byteshaft.restaurantadmin.utils.Helpers;

import org.json.JSONArray;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_details);
        mTableNumber = (EditText) findViewById(R.id.table_number_edit_text);
        mMinimumBookingTime = (EditText) findViewById(R.id.minimum_booking_edit_text);
        mChair = (EditText) findViewById(R.id.chairs_edit_text);
        mTableLocation = (EditText) findViewById(R.id.table_location_edit_text);
        mSaveButton = (Button) findViewById(R.id.save_button);

        mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (validate()) {
            addTableDetails(mTableNumberString, mTableNumberString, mMinimumBookingTimeString, mTableLocationString);
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
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String tableNumber = jsonObject.getString(AppGlobals.KEY_TABLE_NUMBER);
                            String tableChairs = jsonObject.getString(AppGlobals.KEY_TABLE_CHAIRS);
                            String bookingTime = jsonObject.getString(AppGlobals.KEY_TABLE_BOOKING_TIME);
                            String tableLocation = jsonObject.getString(AppGlobals.KEY_TABLE_LOCATION);
                            String tableId = jsonObject.getString(AppGlobals.KEY_TABLE_ID);
                            String restaurantId = jsonObject.getString(AppGlobals.KEY_RESTAURANT_ID);
                            String tableStatus = jsonObject.getString(AppGlobals.KEY_TABLE_STATUS);
                            

                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TABLE_NUMBER, tableNumber);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TABLE_CHAIRS, tableChairs);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TABLE_BOOKING_TIME, bookingTime);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TABLE_LOCATION, tableLocation);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TABLE_ID, tableId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_RESTAURANT_ID, restaurantId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TABLE_STATUS, tableStatus);
                            finish();
                            startActivity(new Intent(TableDetails.this, MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }

    }

    private void addTableDetails(String tableNumber, String numberOfChairs, String minimumBookingTime, String location) {
        mRequest = new HttpRequest(this);
        mRequest.setOnReadyStateChangeListener(this);
        mRequest.setOnErrorListener(this);
        mRequest.open("POST", String.format("%srestaurant/tables/", AppGlobals.BASE_URL));
        mRequest.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        mRequest.send(getTableData(tableNumber, numberOfChairs, minimumBookingTime, location));
        Helpers.showProgressDialog(TableDetails.this, "updating your profile..");
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

    public boolean validate() {
        boolean valid = true;
        mTableNumberString = mTableNumber.getText().toString();
        mMinimumBookingTimeString = mMinimumBookingTime.getText().toString();
        mChairString = mChair.getText().toString();
        mTableLocationString = mTableLocation.getText().toString();
        System.out.println(mChairString + "chairs");

        if (mTableNumberString.trim().isEmpty()) {
            mTableNumber.setError("please table number");
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
