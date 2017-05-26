package com.byteshaft.restaurantadmin.restaurantfragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.restaurantadmin.R;
import com.byteshaft.restaurantadmin.utils.AppGlobals;
import com.byteshaft.restaurantadmin.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by husnain on 5/9/17.
 */

public class Promotions extends Fragment implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener, View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private View mBaseView;
    private EditText mPromotion;
    private EditText mStartDate;
    private EditText mEndDate;

    private Button mSendButton;

    private String mStartDateString;
    private String mEndDateString;
    private String mPromotionString;
    private String mTitleString;

    private boolean isEndTime = false;

    private DatePickerDialog datePickerDialog;
    private HttpRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_promotions, container, false);
        mPromotion = (EditText) mBaseView.findViewById(R.id.promotions_edit_text);
        mStartDate = (EditText) mBaseView.findViewById(R.id.start_date_edit_text);
        mEndDate = (EditText) mBaseView.findViewById(R.id.end_date_edit_text);
        mSendButton = (Button) mBaseView.findViewById(R.id.button_send);
        mSendButton.setOnClickListener(this);
        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return mBaseView;
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send:
                mPromotionString = mPromotion.getText().toString();
                mStartDateString = mStartDate.getText().toString();
                mEndDateString = mEndDate.getText().toString();
                mTitleString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_RESTAURANT_NAME);
                restaurantPromotions(mStartDateString, mEndDateString, mPromotionString, mTitleString);
                break;
            case R.id.start_date_edit_text:
                datePickerDialog.show();
                break;
            case R.id.end_date_edit_text:
                datePickerDialog.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (!isEndTime) {
            mStartDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            isEndTime = true;
        } else {
            mEndDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            isEndTime = false;
        }
    }

    private void restaurantPromotions(String startDate, String endDate, String description, String title) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%spromotions", AppGlobals.BASE_URL));
        request.send(getPromotionsData(startDate, endDate, description, title));
        Helpers.showProgressDialog(getActivity(), "Sending promotions..");
    }

    private String getPromotionsData(String startDate, String endDate, String description, String title) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("start_date", startDate);
            jsonObject.put("end_date", endDate);
            jsonObject.put("description", description);
            jsonObject.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }
}
