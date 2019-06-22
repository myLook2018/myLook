package com.mylook.mylook.session;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.explore.ExploreFragment;
import com.mylook.mylook.home.HomeFragment;
import com.mylook.mylook.profile.ProfileFragment;
import com.mylook.mylook.recommend.RecommendFragment;

public class Session {
    private static Session singleton = null;
    public static final int HOME_FRAGMENT = 1;
    public static final int EXPLORE_FRAGMENT = 2;
    public static final int RECOMMEND_FRAGMENT = 3;
    public static final int CLOSET_FRAGMENT = 4;
    public static final int PROFILE_FRAGMENT = 5;
    public static final String TAG = "Session";
    public static String userId = null;
    public static boolean isPremium = false;
    public static String name = "";
    public static String mail = "";
    public static String clientId = "";

    private Session() {}

    public static Session getInstance() {
        if (singleton == null) {
            singleton = new Session();
        }
        return singleton;
    }

    /**
     * Por cada número que se le pasa reestablece el estado del fragmento para que este muestre
     * lo nuevo.
     *
     * @param fragments Cada uno de los fragmentos que fue modificado y que necesitan recargarse. Utilizar
     *                  los números propios de la clase.
     */
    public void updateActivitiesStatus(int... fragments) {
        for (int f : fragments) {
            Log.e(TAG, "Esta actualizando " + f);
            switch (f) {
                case RECOMMEND_FRAGMENT:
                    RecommendFragment.getInstance().refreshStatus();
                    break;
                case CLOSET_FRAGMENT:
                    ClosetFragment.getInstance().refreshStatus();
                    break;
                case PROFILE_FRAGMENT:
                    ProfileFragment.getInstance().refreshStatus();
                    break;
            }
        }
    }

    public static void updateData() {
        FirebaseFirestore.getInstance().collection("clients").document(clientId).get()
                .addOnSuccessListener(document -> {
                    isPremium = (Boolean) document.get("isPremium");
                    name = document.get("name").toString() + " " + document.get("surname").toString();
                    mail = (String) document.get("email");
                });
    }

    public boolean isPremiumUser() {
        return isPremium;
    }
}
