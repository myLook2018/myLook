package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.mylook.mylook.premiumUser.NewDiffusionMessage;
import com.mylook.mylook.premiumUser.NewPublicationActivity;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.session.Session;

import static com.mylook.mylook.R.drawable.ic_channel;
import static com.mylook.mylook.R.drawable.ic_megaf;
import static com.mylook.mylook.R.drawable.ic_new_diffusion;
import static com.mylook.mylook.R.drawable.ic_portrait;
import static com.mylook.mylook.R.drawable.ic_premium_profile;
import static com.mylook.mylook.R.drawable.ic_premium_publication;
import static com.mylook.mylook.R.drawable.ic_settings;
import static com.mylook.mylook.R.drawable.ic_usuario_verificado;

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
        imgProfile.setImageDrawable(getResources().getDrawable(ic_usuario_verificado));
        myProfile.setOnClickListener(v -> {
            Intent intent= new Intent(mContext, PremiumUserProfileActivity.class);
            intent.putExtra("clientId", Session.clientId);
            intent.putExtra("isCurrent",true);
            startActivity(intent);
        });

        LinearLayout newPub = view.findViewById(R.id.incNewPublication);
        TextView lblNewPublication = (TextView) newPub.findViewById(R.id.lblNameOption);
        lblNewPublication.setText("Nuevo Post");
        ImageView imgNewPublication= newPub.findViewById(R.id.iconPremiumOption);
        imgNewPublication.setImageDrawable(getResources().getDrawable(ic_portrait));
        newPub.setOnClickListener(v -> {
            Intent intent= new Intent(mContext, NewPublicationActivity.class);
            intent.putExtra("clientId", Session.clientId);
            startActivity(intent);
        });

        LinearLayout newGroup = view.findViewById(R.id.incNewGroup);
        TextView lblNewGroup = (TextView) newGroup.findViewById(R.id.lblNameOption);
        lblNewGroup.setText("Tus Datos Públicos");
        ImageView imgNewGroup= newGroup.findViewById(R.id.iconPremiumOption);
        imgNewGroup.setImageDrawable(getResources().getDrawable(ic_settings));
        newGroup.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PremiumRequestActivity.class);
            intent.putExtra("clientId", Session.clientId);
            intent.putExtra("isChange",true);
            startActivity(intent);
        });

        LinearLayout newMess = view.findViewById(R.id.incNewMess);
        TextView lblNewMess = (TextView) newMess.findViewById(R.id.lblNameOption);
        lblNewMess.setText("Enviar Difusión");
        ImageView imgNewDiffusion= newMess.findViewById(R.id.iconPremiumOption);
        imgNewDiffusion.setImageDrawable(getResources().getDrawable(ic_megaf));
        newMess.setOnClickListener(v -> {
            startActivity(new Intent(mContext, NewDiffusionMessage.class));
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

    public void clear() {
        premiumOptionsInstance=null;
    }
}