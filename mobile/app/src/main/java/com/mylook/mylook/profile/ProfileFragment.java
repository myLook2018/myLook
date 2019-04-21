package com.mylook.mylook.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.session.Sesion;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {

    }

    private TextView txtName;
    private TextView txtEmail;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView imageGroup;
    private ImageView imageDestacado;
    private TextView txtGroup;
    private TextView txtDestacado;
    private ImageView imageAccount;
    private TextView txtAccount;
    private ImageView imageHelp;
    private TextView txtHelp;
    private ImageView imageExit;
    private TextView txtExit;
    private String dbUserId = Sesion.getInstance().getSessionUserId();
    private Context mContext;
    private String clientId;
    private boolean isPremiumUser;
    private String userName;
    private static boolean loaded = false;
    public final static String TAG = "ProfileFragment";

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
            txtName.setText(userName);
            txtName.setVisibility(View.VISIBLE);
            txtEmail.setText(user.getEmail().equals("") ? "" : user.getEmail());
            txtEmail.setVisibility(View.VISIBLE);
            if (isPremiumUser) {
                imageGroup.setVisibility(View.VISIBLE);
                txtGroup.setVisibility(View.VISIBLE);
            } else {
                imageDestacado.setVisibility(View.VISIBLE);
                txtDestacado.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initElements(View view) {
        mContext = getContext();
        txtEmail = view.findViewById(R.id.txtEmail);
        txtName = view.findViewById(R.id.txtName);
        imageGroup = view.findViewById(R.id.image_group);
        imageDestacado = view.findViewById(R.id.image_destacado);
        txtGroup = view.findViewById(R.id.txtDifussionGroup);
        txtDestacado = view.findViewById(R.id.txtSettings);
        imageAccount = view.findViewById(R.id.image_account);
        txtAccount = view.findViewById(R.id.txtAccount);
        imageHelp = view.findViewById(R.id.image_help);
        txtHelp = view.findViewById(R.id.txtHelp);
        imageExit = view.findViewById(R.id.image_exit);
        txtExit = view.findViewById(R.id.txtExit);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, null);
    }

    private void setOnClickListener() {

        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyAccountActivity.class);
                startActivity(intent);
            }
        });

        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyAccountActivity.class);
                startActivity(intent);
            }
        });


        imageDestacado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PremiumRequestActivity.class);
                intent.putExtra("clientId", clientId);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });
        txtDestacado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PremiumRequestActivity.class);
                intent.putExtra("clientId", clientId);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });


        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PremiumUserProfileActivity.class);
                intent.putExtra("clientId", clientId);
                startActivity(intent);
            }
        });
        txtGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PremiumUserProfileActivity.class);
                intent.putExtra("clientId", clientId);
                startActivity(intent);
            }
        });


        imageHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        txtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txtExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dm = DialogManager.getInstance();

                dm.createLogoutDialog(mContext,
                        "Cerrar Sesion",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show();
            }
        });

        imageExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dm = DialogManager.getInstance();

                dm.createLogoutDialog(
                        mContext,
                        "Cerrar Sesion",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show();
            }
        });


    }

    private void setUserProfile() {
        dB.collection("clients").whereEqualTo("userId", dbUserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                userName = task.getResult().getDocuments().get(0).get("name").toString() + " " + task.getResult().getDocuments().get(0).get("surname").toString();
                isPremiumUser = (boolean) task.getResult().getDocuments().get(0).get("isPremium");
                loaded = true;
                txtName.setText(userName);
                clientId = task.getResult().getDocuments().get(0).getId();
                txtEmail.setText(user.getEmail().equals("") ? "" : user.getEmail());

                if (isPremiumUser) {
                    imageGroup.setVisibility(View.VISIBLE);
                    txtGroup.setVisibility(View.VISIBLE);
                    //layoutDifussionGroup.setVisibility(View.VISIBLE);
                } else {
                    imageDestacado.setVisibility(View.VISIBLE);
                    txtDestacado.setVisibility(View.VISIBLE);
                    //layoutPremiumRequest.setVisibility(View.VISIBLE);
                }
            }
        });


    }

}