package com.byteshaft.restaurantadmin.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.byteshaft.restaurantadmin.MainActivity;
import com.byteshaft.restaurantadmin.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Service extends FirebaseMessagingService {
//    private String message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("TAG", remoteMessage.getData().toString());
        if (remoteMessage.getData().get("type").equals("booking")) {
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("content"));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Table No " + jsonObject.getInt("table"));
                stringBuilder.append(" Start Time " + jsonObject.getString("start_time"));
                stringBuilder.append("End Time " + jsonObject.getString("end_time"));
//                stringBuilder.append(" User "+ jsonObject.getString("booker"));
                JSONArray jsonArray = jsonObject.getJSONArray("order");
                StringBuilder orderDetails = new StringBuilder();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    orderDetails.append(json.getString("name") + " (" + json.getDouble("price") + ")");
                }
                stringBuilder.append("order menu " + orderDetails.toString());
                sendNotification("New Table Booking", stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (remoteMessage.getData().get("type").equals("geo_fence")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(remoteMessage.getData().get("content"));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Customer " + jsonObject.getString("booker") + " Arrived \n");
                stringBuilder.append("Order: " + jsonObject.getString("order_details")
                        .replace("[", "").replace("]", ""));
                stringBuilder.append("Table No " + jsonObject.getInt("table"));
                sendNotification("Table Booking", stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(bm)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
