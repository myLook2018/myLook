package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AppComponentFactory;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.mylook.mylook.session.Sesion;

public class AccountActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "On view Created - is Loaded? " + loaded);
        if (!loaded) {
            initElements();
            setOnClickListener();
            setUserProfile();
        } else {
            initElements();
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

    private void initElements() {
        setContentView(R.layout.fragment_profile);
        mContext = AccountActivity.this;
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        imageGroup = findViewById(R.id.image_group);
        imageDestacado =findViewById(R.id.image_destacado);
        txtGroup = findViewById(R.id.txtDifussionGroup);
        txtDestacado = findViewById(R.id.txtSettings);
        imageAccount = findViewById(R.id.image_account);
        txtAccount = findViewById(R.id.txtAccount);
        imageHelp = findViewById(R.id.image_help);
        txtHelp = findViewById(R.id.txtHelp);
        imageExit = findViewById(R.id.image_exit);
        txtExit = findViewById(R.id.txtExit);
    }


    private void setOnClickListener() {

        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyAccountActivity.class);
                startActivity(intent);
            }
        });

        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyAccountActivity.class);
                startActivity(intent);
            }
        });


        imageDestacado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PremiumRequestActivity.class);
                intent.putExtra("clientId", clientId);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });
        txtDestacado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PremiumRequestActivity.class);
                intent.putExtra("clientId", clientId);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });


        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PremiumUserProfileActivity.class);
                intent.putExtra("clientId", clientId);
                startActivity(intent);
            }
        });
        txtGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PremiumUserProfileActivity.class);
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
