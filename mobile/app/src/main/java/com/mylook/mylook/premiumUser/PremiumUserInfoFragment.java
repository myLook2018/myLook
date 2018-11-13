package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Subscription;

public class PremiumUserInfoFragment extends Fragment{
    private  boolean isCurrentUser;
    private FirebaseUser user;
    private FirebaseFirestore dB;
    private ImageView profilePhoto;
    private Button btnSubscribe,btnMoreInfo;
    private TextView premiumName;
    private TextView txtEmail;
    private Context context;
    private String clientId;
    private boolean mSubscribed;
    private String documentId="";
    private TextView txtLocalization;
    private TextView txtFacebook,txtInstagram;
    private LinearLayout lnlface,lnlInsta;


    @SuppressLint("ValidFragment")
    public PremiumUserInfoFragment(Context context, String clientId, boolean isCurrentUser) {
        dB = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.context=context;
        this.clientId=clientId;
        this.isCurrentUser=isCurrentUser;

    }

    public PremiumUserInfoFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_premium, container, false);
        initElements(rootView);
        if(!isCurrentUser){
            checkFollow();
            setOnClickSubscribe();
            Log.e("ISCURRENTUSER",String.valueOf(isCurrentUser));
        }
        return rootView;
    }

    public void setOnClickFacebook(String txtFacebook) {
        if(txtFacebook!=""){
            final String finalTxtFacebook = "https://www.facebook.com/"+txtFacebook;
            this.txtFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook));
                    intent.setPackage("com.facebook.android");
                    try{
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtFacebook)));
                    }
                }
            });
            lnlface.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickInstagram(String txtInstagram) {
        if(txtInstagram!="") {
            final String finalTxtInstagram = "http://instagram.com/"+txtInstagram;
            this.txtInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram));
                    intent.setPackage("com.instagram.android");
                    try{
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtInstagram)));
                    }
                }
            });
            lnlInsta.setVisibility(View.VISIBLE);
        }
    }

    public void setTxtLocalization(String txtLocalization) {
        this.txtLocalization.setText(txtLocalization);
    }

    public void setProfilePhoto(String profilePhoto) {
        Glide.with(context).load(profilePhoto).into(this.profilePhoto);
    }


    public void setPremiumName(String storeName) {
        this.premiumName.setText(storeName);
    }

    public void setTxtEmail(String txtEmail) {
        this.txtEmail.setText(txtEmail);
    }
    public void setOnClickSubscribe(){
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubscribe.setEnabled(false);
                if (!mSubscribed) {

                    Subscription newSubscription = new Subscription(clientId, user.getUid());

                    dB.collection("premiumUsersSubscriptions").add(newSubscription).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Firestore task", "DocumentSnapshot written with ID: " + documentReference.getId());
                            documentId = documentReference.getId();
                            setupButtonSubscribe(true);
                            displayMessage("Ahora estas suscripto a "+premiumName.getText().toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore task", "Error adding document", e);
                            btnSubscribe.setEnabled(true);
                        }
                    });

                } else {
                    dB.collection("premiumUsersSubscriptions").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setupButtonSubscribe(false);
                                documentId = "";
                                Log.e("BUTTON",documentId);
                                displayMessage("Ya no estás suscripto");
                            }
                            btnSubscribe.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void setupButtonSubscribe(boolean subscribed) {

        if (subscribed) {
            btnSubscribe.setText("Desuscribirse");
            btnSubscribe.setBackgroundColor( getResources().getColor(R.color.primary_dark));
            mSubscribed = true;
        } else {
            btnSubscribe.setText("Suscribirse");
            btnSubscribe.setBackgroundColor(getResources().getColor(R.color.accent));
            mSubscribed=false;
        }

        btnSubscribe.setEnabled(true);
    }
    public void checkFollow() {
        btnSubscribe.setVisibility(View.VISIBLE);
        dB.collection("premiumUsersSubscriptions")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("storeName", clientId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        //read subscription id
                        documentId = task.getResult().getDocuments().get(0).getId();
                        mSubscribed=true;
                    }
                    setupButtonSubscribe(!task.getResult().isEmpty());
                }
            }
        });
    }
    public void initElements(View rootView){
        profilePhoto = rootView.findViewById(R.id.premium_profile_photo);
        btnSubscribe =rootView.findViewById(R.id.btn_subscribe);
        premiumName =rootView.findViewById(R.id.profile_premium_name);
        txtEmail=rootView.findViewById(R.id.txtEmail);
        txtLocalization=rootView.findViewById(R.id.txtLocalization);
        txtInstagram=rootView.findViewById(R.id.txtInstagram);
        txtFacebook=rootView.findViewById(R.id.txtFacebook);
        lnlface=rootView.findViewById(R.id.lnlFace);
        lnlInsta=rootView.findViewById(R.id.lnlInta);

    }

    private void displayMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
