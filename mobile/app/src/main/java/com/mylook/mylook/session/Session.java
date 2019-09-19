package com.mylook.mylook.session;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.profile.PremiumOptionsFragment;
import com.mylook.mylook.recommend.RecommendFragment;

/**
 * Lo defino como un servicio en caso de que en un futuro tengamos que hacer tareas,
 * pero por ahora se va a manejar como una clase Singleton.
 */
public class Session {

    private static Session singleton = null;
    public static final int HOME_FRAGMENT = 1;
    public static final int EXPLORE_FRAGMENT = 2;
    public static final int RECOMEND_FRAGMENT = 3;
    public static final int CLOSET_FRAGMENT = 4;
    public static final int PROFILE_FRAGMENT = 5;
    public static final String TAG = "Sesion";
    public static String userId = null;
    public static boolean isPremium;
    public static String name= "";
    public static String mail= "";
    public static String clientId= "";

    private Session() {

    }

    public boolean isPremiumUser(){
        return isPremium;
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
                case HOME_FRAGMENT:
                    //HomeFragment.getInstance().refreshStatus();
                    break;
                case EXPLORE_FRAGMENT:
                    //ExploreFragment.getInstance().refreshStatus();
                    break;
                case RECOMEND_FRAGMENT:
                    RecommendFragment.getInstance().refreshStatus();
                    break;
                case CLOSET_FRAGMENT:
                    ClosetFragment.getInstance().refreshStatus();
                    break;
                case PROFILE_FRAGMENT:
                    PremiumOptionsFragment.getInstance().refreshStatus();
                    break;
            }
        }
    }

    /**
     * @return Singleton object Sesion
     */
    public static Session getInstance() {
        if (singleton == null) {
            Log.e("getInstance", "singleton ==null" );
            singleton = new Session();
        }
        return singleton;
    }

    public Task<QuerySnapshot> initializeElements() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                return FirebaseFirestore.getInstance().collection("clients").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        userId = document.get("userId").toString();
                        isPremium = (boolean) document.get("isPremium");
                        name = document.get("name").toString() + " " + document.get("surname").toString();
                        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        clientId = document.getId();
                    } else {
                        FirebaseFirestore.getInstance().collection("clients").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                userId = document.get("userId").toString();
                                isPremium = (Boolean) document.get("isPremium");
                                name = document.get("name").toString() + " " + document.get("surname").toString();
                                mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                clientId = document.getId();
                                //cuando entra aca?
                            }
                        });
                    }
                });
        }
        return null;
    }

    public static void updateData(){
        FirebaseFirestore.getInstance().collection("clients").document(clientId).get()
                .addOnSuccessListener(document -> {
            isPremium = (boolean) document.get("isPremium");
            Log.e("SESSION","is premium: "+isPremium);
            name = document.get("name").toString() + " " + document.get("surname").toString();
            mail = (String) document.get("email");
        });
    }
}