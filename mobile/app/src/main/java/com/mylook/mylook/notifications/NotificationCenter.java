package com.mylook.mylook.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Notification;
import com.mylook.mylook.premiumUser.MessagesRecyclerViewAdapter;
import com.mylook.mylook.premiumUser.NewDiffusionMessage;

import java.util.ArrayList;

public class NotificationCenter extends Activity {
    private ArrayList<Notification> notifications;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Toolbar tb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onResume() {
        super.onResume();
        initElements();
    }

    private void initElements(){
        setContentView(R.layout.activity_notification_center);
        recyclerView = findViewById(R.id.notification_recycler);
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Notificaciones");
        updateNotifications();
        getNotifications();
    }


    private void initRecyclerView() {
        adapter = new NotificationRecyclerViewAdapter(NotificationCenter.this, notifications);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);

    }

    private void getNotifications(){
        if (notifications == null || notifications.size() == 0)
            notifications = new ArrayList<>();
        initRecyclerView();
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(v -> {
                    if (v.getDocuments().size() > 0){
                        for(DocumentSnapshot doc: v.getDocuments()){
                            Notification notif = doc.toObject(Notification.class);
                            notif.setDocumentId(doc.getId());
                            notifications.add(notif);
                        }
                        Log.e("Notifications", ""+notifications.size());
                        adapter.notifyDataSetChanged();
                    }
        });
    }

    private void updateNotifications(){
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getUid())
                .whereEqualTo("openedNotification", false)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(v -> {
            for(DocumentSnapshot doc: v.getDocuments()){
                FirebaseFirestore.getInstance().collection("notifications").document(doc.getId())
                        .update("openedNotification", true);
            }
        });
    }
}
