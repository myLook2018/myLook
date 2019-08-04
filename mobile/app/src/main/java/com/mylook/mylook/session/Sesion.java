package com.mylook.mylook.session;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.explore.ExploreFragment;
import com.mylook.mylook.home.HomeFragment;
import com.mylook.mylook.profile.ProfileFragment;
import com.mylook.mylook.recommend.RecommendFragment;

/**
 * Lo defino como un servicio en caso de que en un futuro tengamos que hacer tareas,
 * pero por ahora se va a manejar como una clase Singleton.
 */
public class Sesion extends Service {
    private static Sesion singleton = null;
    public static final int HOME_FRAGMENT = 1;
    public static final int EXPLORE_FRAGMENT = 2;
    public static final int RECOMEND_FRAGMENT = 3;
    public static final int CLOSET_FRAGMENT = 4;
    public static final int PROFILE_FRAGMENT = 5;
    public static final String TAG = "Sesion";
    public  String userId = null;
    public static boolean isPremium = false;
    public static String name= "";
    public static String mail= "";
    public static String clientId= "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Sesion() {
        Log.e(TAG, "Constructor sesion");
        getUserId();
    }

    public boolean isPremiumUser(){
        return isPremium;
    }

    public String getSessionUserId(){
        return singleton.userId;
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
                /*case HOME_FRAGMENT:
                    HomeFragment.getInstance().refreshStatus();
                    break;
                case EXPLORE_FRAGMENT:
                    ExploreFragment.getInstance().refreshStatus();
                    break;*/
                case RECOMEND_FRAGMENT:
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

    /**
     * @return Singleton object Sesion
     */
    public static Sesion getInstance() {
        if (singleton == null) {
            Log.e(TAG, "Singleton is null");
            singleton = new Sesion();
        }
        return singleton;
    }

    public void setUserId(String userId) {
        singleton.userId = userId;
    }

    private void getMailUserId(){
        db.collection("clients").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e(TAG,"getEmailUser");
                if (task.isSuccessful()) {
                    userId = task.getResult().getDocuments().get(0).get("userId").toString();
                    Log.e(TAG, "userId"+userId);
                    singleton.setUserId(userId);
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failure: "+e.getMessage()));
    }

    private void getUserId() {
        Log.e(TAG,"getUserId");
        String id = FirebaseAuth.getInstance().getUid();
        Log.e(TAG,"id"+id);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.e(TAG,"User is NOT null");
                db.collection("clients").whereEqualTo("userId", id).get().addOnCompleteListener(task -> {
                    Log.e(TAG, "Query ready");
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        userId = task.getResult().getDocuments().get(0).get("userId").toString();
                        Log.e(TAG, "COmpleted: "+userId);
                        singleton.setUserId(userId);
                    } else {
                        Log.e(TAG,"User not found");
                        getMailUserId();
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Failure: "+e.getMessage()));

        } else{
            Log.e(TAG,"Current User is null");
            getMailUserId();
        }
    }

    public void updateData(){
        db.collection("clients").document(clientId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                isPremium = (Boolean) document.get("isPremium");
                name = document.get("name").toString() + " " + document.get("surname").toString();
                mail = (String) document.get("email");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
