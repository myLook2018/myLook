package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.session.Session;

public class ProfileFragment extends Fragment {

    private Context mContext;

    public ProfileFragment() {

    }

    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean isPremiumUser;
    private String userName;
    private static boolean loaded = false;
    public final static String TAG = "PremiumOptions";

    private static ProfileFragment homeInstance = null;

    public static ProfileFragment getInstance() {
        if (homeInstance == null) {
            homeInstance = new ProfileFragment();
        }
        return homeInstance;
    }

    public static void refreshStatus(){
        if(homeInstance!=null){
            loaded = false;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "On view Created - is Loaded? "+loaded);
        super.onViewCreated(view, savedInstanceState);
        if (!loaded) {
            initElements(view);
            setOnClickListener();
            setUserProfile();
        } else {
            initElements(view);
            setOnClickListener();

        }
    }

    private void initElements(View view) {
        mContext = getContext();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_premium_func, null);
    }

    private void setOnClickListener() {

    }

    private void setUserProfile() {


    }

}
