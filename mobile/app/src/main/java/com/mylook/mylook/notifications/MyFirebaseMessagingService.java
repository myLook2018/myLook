package com.mylook.mylook.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mylook.mylook.R;
import com.mylook.mylook.home.MyLookActivity;
import com.mylook.mylook.session.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "Llego un mensaje nuevo");

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        if (!remoteMessage.getData().isEmpty()) {
            Log.e("Data", remoteMessage.getData().toString());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(notificationTitle, notificationBody);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.e(TAG, "Entered in deleted messages");
    }

    private void sendNotification(String notificationTitle, String notificationBody) {
        Notification notification;
        Log.e(TAG, "sendNotification");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "Version Oreo o Más");
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = "default";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
             notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setSmallIcon(R.drawable.ic_icon_logo)
                     .setColorized(true )
                     .setColor(getResources().getColor(R.color.purple))
                    .setChannelId(CHANNEL_ID)
                    .build();
            mNotificationManager.notify(notifyID , notification);
        } else {
            Log.e(TAG, "Version < Oreo");
            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.ic_icon_logo);
            notification = notificationBuilder.build();
            Log.e("Notification", notification.toString());
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify("default", 123, notification);
        }
    }


}
