package com.byteshaft.restaurantadmin.restaurantfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byteshaft.restaurantadmin.R;

/**
 * Created by husnain on 5/9/17.
 */

public class Promotions extends Fragment {

    private View mBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_promotions, container, false);
        return mBaseView;
    }
}
