package com.johnmorrison.restaurantadmin.accountfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.johnmorrison.restaurantadmin.MainActivity;
import com.byteshaft.restaurantadmin.R;
import com.johnmorrison.restaurantadmin.utils.AppGlobals;

/**
 * Created by s9iper1 on 3/16/17.
 */

public class AccountManagerActivity extends AppCompatActivity {

    private static AccountManagerActivity sInstance;

    public static AccountManagerActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppGlobals.isLogin()) {
            loadFragment(new Login());
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        setContentView(R.layout.activity_account_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sInstance = this;
    }


    public void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container, fragment, backStateName);
        FragmentManager manager = getSupportFragmentManager();
        Log.i("TAG", backStateName);
////        if (fragment.isVisible()) {
//        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
//        if (!fragmentPopped) {
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
