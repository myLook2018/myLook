package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;

public class ProfileFragment extends Fragment {

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
    private Context mContext;
    private String clientId;
    private boolean isPremiumUser;
    private String userName;
    private static boolean loaded = false;
    public final static String TAG = "ProfileFragment";
    private static ProfileFragment profileInstance = null;

    public static ProfileFragment getInstance() {
        if (profileInstance == null) {
            profileInstance = new ProfileFragment();
        }
        return profileInstance;
    }

    public static void refreshStatus() {
        if (profileInstance != null) loaded = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "On view Created - is Loaded? " + loaded);
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
        txtAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditInfoActivity.class);
            startActivity(intent);
        });
        imageAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditInfoActivity.class);
            startActivity(intent);
        });
        imageDestacado.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PremiumRequestActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("userName", userName);
            startActivity(intent);

        });
        txtDestacado.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PremiumRequestActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("userName", userName);
            startActivity(intent);

        });
        imageGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PremiumUserProfileActivity.class);
            intent.putExtra("clientId", clientId);
            startActivity(intent);
        });
        txtGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PremiumUserProfileActivity.class);
            intent.putExtra("clientId", clientId);
            startActivity(intent);
        });
        imageHelp.setOnClickListener(v -> {
            // TODO ?????
        });
        txtHelp.setOnClickListener(v -> {
            // TODO ?????
        });
        txtExit.setOnClickListener(v ->
                DialogManager.createLogoutDialog(mContext,
                        "Cerrar Session",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show());
        imageExit.setOnClickListener(v ->
                DialogManager.createLogoutDialog(mContext,
                        "Cerrar Session",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show());
    }

    private void setUserProfile() {
        dB.collection("clients").whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
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
        });
    }

}
