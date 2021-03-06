package com.johnmorrison.restaurantadmin.fcm;

import android.util.Log;

import com.johnmorrison.restaurantadmin.utils.AppGlobals;
import com.johnmorrison.restaurantadmin.utils.Helpers;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FireBaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FCM_TOKEN, token);
        Log.e("TAG", "Token " + token);
        if (AppGlobals.isLogin()) {
            Helpers.sendKey();
        }
    }
}
