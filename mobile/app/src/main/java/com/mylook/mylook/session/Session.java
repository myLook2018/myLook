package com.mylook.mylook.session;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class Session {

    private static Session singleton = null;
    private static final String TAG = "Session";
    public static String userId = null;
    public static boolean isPremium = false;
    public static String name = "";
    public static String mail = "";
    public static String clientId = ""; // TODO value not changed

    private static boolean subscriptionAdded;
    private static boolean favoriteAdded;

    private Session() {}

    public static Session getInstance() {
        if (singleton == null) {
            singleton = new Session();
        }
        return singleton;
    }

    public static void updateData() {
        Log.d(TAG, "updateData: Getting client data.");
        FirebaseFirestore.getInstance().collection("clients").document(clientId).get()
                .addOnSuccessListener(document -> {
                    isPremium = (Boolean) document.get("isPremium");
                    name = document.get("name").toString() + " " + document.get("surname").toString();
                    mail = (String) document.get("email");
                    Log.d(TAG, "updateData: Client data retrieved successfully.");
                });
    }

    public boolean isPremiumUser() {
        return isPremium;
    }

    public void setSubscriptionAdded(boolean added) {
        subscriptionAdded = added;
    }
    
    public void setFavoriteAdded(boolean added) {
        favoriteAdded = added;
    }
    
    public boolean doesHomeUpdate() {
        boolean update = subscriptionAdded;
        subscriptionAdded = false;
        return update;
    }

    public boolean doesClosetUpdate() {
        boolean update = favoriteAdded;
        favoriteAdded = false;
        return update;
    }
}
