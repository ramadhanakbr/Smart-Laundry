package com.example.smartlaundry.utils;

import android.app.PendingIntent;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.example.smartlaundry.R;
import com.example.smartlaundry.activity.admin.ListBasketAdmin;
import com.example.smartlaundry.activity.admin.ListOrderAdmin;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFireBaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(getString(R.string.DEBUG_TAG), "New Token: "+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(getString(R.string.DEBUG_TAG),"Message received");
        if (remoteMessage.getNotification().getTitle().equals("Update Orderan")){
            ((App)getApplication()).triggerNotification(ListOrderAdmin.class,
                    getString(R.string.NEWS_CHANNEL_ID),
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getNotification().getBody(),
                    NotificationCompat.PRIORITY_HIGH,
                    true,
                    getResources().getInteger(R.integer.notificationId),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }else if (remoteMessage.getNotification().getTitle().equals("Update Keranjang")){
            ((App)getApplication()).triggerNotification(ListBasketAdmin.class,
                    getString(R.string.NEWS_CHANNEL_ID),
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getNotification().getBody(),
                    NotificationCompat.PRIORITY_HIGH,
                    true,
                    getResources().getInteger(R.integer.notificationId),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }



    }
}
