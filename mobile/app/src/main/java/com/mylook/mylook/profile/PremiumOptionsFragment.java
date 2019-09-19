package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumPublication;
import com.mylook.mylook.entities.RequestRecommendation;
import com.mylook.mylook.premiumUser.PremiumPublicationsFragment;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.recommend.RequestRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class PremiumOptionsFragment extends ListFragment {
    private static String[] premiumOptionsList = {"Mi Perfil", "Nuevo Grupo de difusion",
            "Nueva publicacion en perfil", "Nuevo mensaje para difundir"};

    private Context mContext;
    private ListView listView;

    public PremiumOptionsFragment() {

    }

    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean isPremiumUser;
    private String userName;
    private static boolean loaded = false;
    public final static String TAG = "PremiumOptions";

    private static PremiumOptionsFragment homeInstance = null;

    public static PremiumOptionsFragment getInstance() {
        if (homeInstance == null) {
            homeInstance = new PremiumOptionsFragment();
        }
        return homeInstance;
    }

    public static void refreshStatus() {
        if (homeInstance != null) {
            loaded = false;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "On view Created - is Loaded? " + loaded);
        initElements(view);
    }

    private void initElements(View view) {
        mContext = getContext();
        listView = view.findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter(getActivity(), R.layout.layout_list_item_premium_option, premiumOptionsList));
        listView.setOnItemClickListener((arg0, v, arg2, arg3) -> {
            switch (arg2){
                case 0:
                    startActivity(new Intent(mContext, PremiumUserProfileActivity.class));
                    break;
                case 1:
                    //startActivity(new Intent(mContext, DifussionGroup.class));
                    break;
                case 2:
                    startActivity(new Intent(mContext, PremiumPublication.class));
                    break;
                case 3:
                    //startActivity(new Intent(mContext, NewDifussionMessage.class));

            }
        });
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

}