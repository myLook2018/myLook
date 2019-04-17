package com.mylook.mylook.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.utils.BottomNavigationViewHelper;

public class ProfileActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 4;
    private LinearLayout layoutAccount;
    private LinearLayout layoutPremiumRequest;
    private LinearLayout layoutHelp;
    private LinearLayout layoutExit;
    private TextView txtName;
    private TextView txtEmail;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Context mContext = ProfileActivity.this;
    private String clientId;
    private boolean isPremiumUser;
    private LinearLayout layoutDifussionGroup;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setUserProfile();
        setOnClickListener();

        setupBottomNavigationView();
    }

    private void setOnClickListener() {
        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyAccountActivity.class);
                startActivity(intent);
            }
        });

        layoutPremiumRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PremiumRequestActivity.class);
                intent.putExtra("clientId",clientId);
                intent.putExtra("userName",userName);
                startActivity(intent);

            }
        });
        layoutDifussionGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PremiumUserProfileActivity.class);
                intent.putExtra("clientId",clientId);
                startActivity(intent);
            }
        });
        layoutHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

                final android.app.AlertDialog alert = dialog.setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro que querés cerrar sesión?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                FirebaseAuth.getInstance().signOut();
                                FacebookSdk.sdkInitialize(getApplicationContext());
                                LoginManager.getInstance().logOut();
                                Toast.makeText(getApplicationContext(), "Cerraste sesión :(", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }


                        }).create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purple));
                        alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
                    }
                });
                alert.show();
            }
        });
    }

    private void setUserProfile() {
        layoutAccount = findViewById(R.id.layoutAccount);
        layoutHelp = findViewById(R.id.layoutHelp);
        layoutExit = findViewById(R.id.layoutExit);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        layoutPremiumRequest = findViewById(R.id.layoutPremiumRequest);
        layoutDifussionGroup=findViewById(R.id.layoutDifussionGroup);
        dB.collection("clients").whereEqualTo("userId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    userName =  task.getResult().getDocuments().get(0).get("name").toString() + " " + task.getResult().getDocuments().get(0).get("surname").toString();
                isPremiumUser=(boolean)task.getResult().getDocuments().get(0).get("isPremium");
                txtName.setText(userName);
                clientId=task.getResult().getDocuments().get(0).getId();


                if(isPremiumUser){
                    layoutDifussionGroup.setVisibility(View.VISIBLE);
                }else
                {
                    layoutPremiumRequest.setVisibility(View.VISIBLE);
                }
                txtEmail.setText(user.getEmail().equals("") ? "" : user.getEmail());
            }
        });


    }




    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
