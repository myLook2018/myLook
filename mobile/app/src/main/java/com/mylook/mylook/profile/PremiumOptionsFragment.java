package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylook.mylook.R;
import com.mylook.mylook.premiumUser.NewPublicationActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.session.Session;

import static com.mylook.mylook.R.drawable.ic_channel;
import static com.mylook.mylook.R.drawable.ic_new_diffusion;
import static com.mylook.mylook.R.drawable.ic_premium_profile;
import static com.mylook.mylook.R.drawable.ic_premium_publication;

public class PremiumOptionsFragment extends ListFragment {

    private Context mContext;
    private LinearLayout listView;

    public PremiumOptionsFragment() {

    }
    private static boolean loaded = false;
    public final static String TAG = "PremiumOptions";

    private static PremiumOptionsFragment premiumOptionsInstance = null;

    public static PremiumOptionsFragment getInstance() {
        if (premiumOptionsInstance == null) {
            premiumOptionsInstance = new PremiumOptionsFragment();
        }
        return premiumOptionsInstance;
    }

    public static void refreshStatus() {
        if (premiumOptionsInstance != null) {
            loaded = false;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "On view Created - is Loaded? " + loaded);
        mContext = view.getContext();
        initElements(view);
    }

    private void initElements(View view) {
        mContext = getContext();
        listView  = view.findViewById(R.id.list);

        LinearLayout myProfile =view.findViewById(R.id.incProfile);
        TextView lblNameOption = (TextView) myProfile.findViewById(R.id.lblNameOption);
        lblNameOption.setText("Mi Perfil");
        ImageView imgProfile= myProfile.findViewById(R.id.iconPremiumOption);
        imgProfile.setImageDrawable(getResources().getDrawable(ic_premium_profile));
        myProfile.setOnClickListener(v -> {
            Intent intent= new Intent(mContext, PremiumUserProfileActivity.class);
            intent.putExtra("clientId", Session.clientId);
            intent.putExtra("isCurrent",true);
            startActivity(intent);
        });

        LinearLayout newPub = view.findViewById(R.id.incNewPublication);
        TextView lblNewPublication = (TextView) newPub.findViewById(R.id.lblNameOption);
        lblNewPublication.setText("Nueva Publicacion");
        ImageView imgNewPublication= newPub.findViewById(R.id.iconPremiumOption);
        imgNewPublication.setImageDrawable(getResources().getDrawable(ic_premium_publication));
        newPub.setOnClickListener(v -> {
            startActivity(new Intent(mContext, NewPublicationActivity.class));
        });

        LinearLayout newGroup = view.findViewById(R.id.incNewGroup);
        TextView lblNewGroup = (TextView) newGroup.findViewById(R.id.lblNameOption);
        lblNewGroup.setText("Nuevo Grupo de difusion");
        ImageView imgNewGroup= newGroup.findViewById(R.id.iconPremiumOption);
        imgNewGroup.setImageDrawable(getResources().getDrawable(ic_channel));
        newGroup.setOnClickListener(v -> {
            //startActivity(new Intent(mContext, DifussionGroup.class));
        });

        LinearLayout newMess = view.findViewById(R.id.incNewMess);
        TextView lblNewMess = (TextView) newMess.findViewById(R.id.lblNameOption);
        lblNewMess.setText("Enviar Mensaje");
        ImageView imgNewDiffusion= newMess.findViewById(R.id.iconPremiumOption);
        imgNewDiffusion.setImageDrawable(getResources().getDrawable(ic_new_diffusion));
        newMess.setOnClickListener(v -> {
            //startActivity(new Intent(mContext, NewDifussionMessage.class));
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_premium_func,null);
    }

}