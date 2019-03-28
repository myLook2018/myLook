package com.mylook.mylook.session;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    int startMode = Service.START_STICKY;
    private static Sesion singleton = null;
    public static final int HOME_FRAGMENT = 1;
    public static final int EXPLORE_FRAGMENT = 2;
    public static final int RECOMEND_FRAGMENT = 3;
    public static final int CLOSET_FRAGMENT = 4;
    public static final int PROFILE_FRAGMENT = 5;
    public static final String TAG = "Sesion";
    public String userId = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Sesion() {
        getUserId();
    }

    public String getSessionUserId(){
        return userId;
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
                    HomeFragment.refreshStatus();
                    break;
                case EXPLORE_FRAGMENT:
                    ExploreFragment.refreshStatus();
                    break;
                case RECOMEND_FRAGMENT:
                    RecommendFragment.refreshStatus();
                    break;
                case CLOSET_FRAGMENT:
                    ClosetFragment.refreshStatus();
                    break;
                case PROFILE_FRAGMENT:
                    ProfileFragment.refreshStatus();
                    break;
            }
        }
    }

    /**
     * @return Singleton object Sesion
     */
    public static Sesion getInstance() {
        if (singleton == null) {
            singleton = new Sesion();
        }

        return singleton;
    }

    public Task initializeElements(){
        return getUserId();
    }

    private Task getUserId() {
           return db.collection("clients").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        userId = task.getResult().getDocuments().get(0).get("userId").toString();

                    } else {
                        db.collection("clients").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    userId = task.getResult().getDocuments().get(0).get("userId").toString();
                                }
                            }
                        });
                    }
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
