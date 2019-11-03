package com.mylook.mylook.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mylook.mylook.R;
import com.mylook.mylook.coupon.CouponActivity;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.recommend.RequestRecommendActivity;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.storeProfile.StoreActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "Llego un mensaje nuevo");

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {
            for (String key : remoteMessage.getData().keySet()) {
                Log.e(TAG, key + ": " + remoteMessage.getData().get(key));
            }
            Log.e(TAG, remoteMessage.getData().toString());
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getData().get("body"));
            notificationTitle = remoteMessage.getData().get("title");
            notificationBody = remoteMessage.getData().get("body");
        }
        String id = "";
        Class activity = null;
        com.mylook.mylook.entities.Notification notif = new com.mylook.mylook.entities.Notification();
        if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsKey("requestId")) {
                id = remoteMessage.getData().get("requestId");
                notif.setMessage(notificationBody);
                notif.setCreationDate(Timestamp.now());
                notif.setOpenedNotification(false);
                notif.setPremiumUserName(remoteMessage.getData().get("storeName"));
                notif.setUserId(FirebaseAuth.getInstance().getUid());
                notif.setElementId(id);
                notif.setOpenClass(getResources().getString(R.string.RecommendClass));
                try {
                    FirebaseFirestore.getInstance().collection("stores")
                            .whereEqualTo("storeName", remoteMessage.getData().get("storeName")).get().addOnSuccessListener(l -> {

                        String storePhoto = (String) l.getDocuments().get(0).get("profilePh");
                        notif.setUserPhotoUrl(storePhoto);
                        FirebaseFirestore.getInstance().collection("notifications").add(notif);

                    });
                } catch (Exception e) {
                    FirebaseFirestore.getInstance().collection("notifications").add(notif);

                }
                activity = RequestRecommendActivity.class;
            } else if (remoteMessage.getData().containsKey("articleId")) {
                id = remoteMessage.getData().get("articleId");
                activity = ArticleInfoActivity.class;
            } else if (remoteMessage.getData().containsKey("storeId") && !remoteMessage.getData().containsKey("voucherCode")) {
                id = remoteMessage.getData().get("storeId");
                activity = StoreActivity.class;
            } else if (remoteMessage.getData().containsKey("topic")) {
                activity = NotificationCenter.class;
                notif.setMessage(notificationBody);
                notif.setTopic(remoteMessage.getData().get("topic"));
                notif.setCreationDate(Timestamp.now());
                notif.setOpenedNotification(false);
                notif.setImageUrl(remoteMessage.getData().get("imageUrl"));
                notif.setPremiumUserName(remoteMessage.getData().get("premiumUserName"));
                notif.setUserPhotoUrl(remoteMessage.getData().get("userImage"));
                notif.setUserId(FirebaseAuth.getInstance().getUid());
                FirebaseFirestore.getInstance().collection("clients")
                        .whereEqualTo("userId",remoteMessage.getData().get("premiumUserId") ).get().addOnSuccessListener(l -> {
                    notif.setElementId(l.getDocuments().get(0).getId());
                    notif.setOpenClass(getResources().getString(R.string.PremiumUserClass));
                    FirebaseFirestore.getInstance().collection("notifications").add(notif);
                });
            } else if (remoteMessage.getData().containsKey("voucherCode")) {
                id = remoteMessage.getData().get("voucherCode");
                activity = CouponActivity.class;
                notif.setMessage(remoteMessage.getData().get("couponTitle"));
                notificationBody = notif.getMessage();
                notif.setCreationDate(Timestamp.now());
                notif.setOpenedNotification(false);
                notif.setPremiumUserName(remoteMessage.getData().get("storeName"));
                notif.setUserId(FirebaseAuth.getInstance().getUid());
                notif.setElementId(remoteMessage.getData().get("voucherCode"));
                notif.setOpenClass(getResources().getString(R.string.CouponClass));
                try {
                    FirebaseFirestore.getInstance().collection("stores")
                            .document(remoteMessage.getData().get("storeId")).get().addOnSuccessListener(l -> {

                        String storePhoto = (String) l.get("profilePh");
                        notif.setUserPhotoUrl(storePhoto);
                        FirebaseFirestore.getInstance().collection("notifications").add(notif);

                    });
                } catch (Exception e) {
                    FirebaseFirestore.getInstance().collection("notifications").add(notif);

                }
            }

        }
        sendNotification(notificationTitle, notificationBody, id, activity);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.e(TAG, "Entered in deleted messages");
    }

    private PendingIntent createIntent(String id, Class activity) {
        Intent newIntent = new Intent(getBaseContext(), activity);
        if (activity == RequestRecommendActivity.class)
            newIntent.putExtra("requestId", id);
        if (activity == StoreActivity.class)
            newIntent.putExtra("storeId", id);
        if (activity == ArticleInfoActivity.class)
            newIntent.putExtra("articleId", id);
        if (activity == CouponActivity.class)
            newIntent.putExtra("couponId", id);
        newIntent.putExtra("fromDeepLink", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    private void sendNotification(String notificationTitle, String notificationBody, String recommendation, Class activity) {
        Notification notification;
        PendingIntent newIntent = createIntent(recommendation, activity);

        //Se hace distinto porque a partir de la versión 8 de Android se utilizan canales de notificaciones
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
                    .setColor(getResources().getColor(R.color.purple))
                    .setContentIntent(newIntent)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(notifyID, notification);
        } else {
            Log.e(TAG, "Version < Oreo");
            Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setAutoCancel(true)
                    .setContentIntent(newIntent)
                    .setColor(getResources().getColor(R.color.purple))
                    .setSmallIcon(R.drawable.ic_icon_logo);

            notification = notificationBuilder.build();
            Log.e("Notification", notification.toString());
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(123, notification);
        }

    }


}