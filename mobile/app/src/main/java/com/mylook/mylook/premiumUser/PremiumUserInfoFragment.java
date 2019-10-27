package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Subscription;

import de.hdodenhof.circleimageview.CircleImageView;

public class PremiumUserInfoFragment extends Fragment {
    private boolean isCurrentUser;
    private FirebaseUser user;
    private CircleImageView profilePhoto;
    private Button btnSubscribe, btnMoreInfo;
    private TextView txtEmail;
    private Context context;
    private String premiumId, userName;
    private boolean mSubscribed;
    private String documentId = "";
    private TextView txtLocalization;
    private LinearLayout lnlface, lnlInsta;
    private TextView lblDate;

    @SuppressLint("ValidFragment")
    public PremiumUserInfoFragment(String premiumId, boolean isCurrentUser) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("ClientId", premiumId);
        this.premiumId = premiumId;
        Log.e("FRAGMENT INFO ", String.valueOf(isCurrentUser));
        this.isCurrentUser = isCurrentUser;
    }

    public PremiumUserInfoFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_premium, container, false);
        initElements(rootView);
        if (!isCurrentUser) {
            checkFollow();
            setOnClickSubscribe();
        }
        return rootView;
    }

    public void setOnClickFacebook(String txtFacebook) {
        if (txtFacebook != "") {
            final String finalTxtFacebook = "https://www.facebook.com/" + txtFacebook;
            this.lnlface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook));
                    intent.setPackage("com.facebook.android");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook)));
                    }
                }
            });
            lnlface.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickInstagram(String txtInstagram) {
        if (txtInstagram != "") {
            final String finalTxtInstagram = "http://instagram.com/" + txtInstagram;
            this.lnlInsta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram));
                    intent.setPackage("com.instagram.android");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram)));
                    }
                }
            });
            lnlInsta.setVisibility(View.VISIBLE);
        }
    }


    public void setProfilePhoto(String profilePhoto) {
        Glide.with(getContext()).load(profilePhoto).into(this.profilePhoto);
    }

    public void suscribeToTopic() {
        FirebaseFirestore.getInstance().collection("clients").document(premiumId).get().addOnSuccessListener(suc -> {
            FirebaseFirestore.getInstance().collection("topics")
                    .whereEqualTo("userId", suc.get("userId")).get()
                    .addOnSuccessListener(v -> {
                        for (DocumentSnapshot doc : v.getDocuments()) {
                            FirebaseMessaging.getInstance().subscribeToTopic((String) doc.get("topic"))
                                    .addOnSuccessListener(vTopic -> {
                                        displayMessage("Ahora estas suscripto a " + userName);
                                        setupButtonSubscribe(true);
                                    }).addOnFailureListener(f -> {
                                Log.e("No se pudo subscribir", f.getMessage());
                                setupButtonSubscribe(true);
                            });

                        }
                    }).addOnFailureListener(f -> {
                Log.e("Error en la query ", f.getMessage());
            });
        });
    }

    public void unsuscribeFromTopic() {
        FirebaseFirestore.getInstance().collection("clients").document(premiumId).get().addOnSuccessListener(suc -> {
            FirebaseFirestore.getInstance().collection("topics")
                    .whereEqualTo("userId", suc.get("userId")).get()
                    .addOnSuccessListener(v -> {
                        for (DocumentSnapshot doc : v.getDocuments()) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic((String) doc.get("topic"))
                                    .addOnSuccessListener(vTopic -> {
                                        displayMessage("Ya no estás suscripto");
                                    }).addOnFailureListener(f -> {
                                displayMessage("No se pudo dessubscribir" + f.getMessage());
                            });


                        }
                    }).addOnFailureListener(f -> {
                displayMessage("Error en la query" + f.getMessage());
            });
        });
    }


    public void setOnClickSubscribe() {
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubscribe.setEnabled(false);
                if (!mSubscribed) {

                    Subscription newSubscription = new Subscription(premiumId, FirebaseAuth.getInstance().getCurrentUser().getUid());

                    FirebaseFirestore.getInstance().collection("premiumUsersSubscriptions").add(newSubscription).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Firestore task", "DocumentSnapshot written with ID: " + documentReference.getId());
                            documentId = documentReference.getId();
                            suscribeToTopic();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore task", "Error adding document", e);
                            btnSubscribe.setEnabled(true);
                        }
                    });

                } else {
                    FirebaseFirestore.getInstance().collection("premiumUsersSubscriptions").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setupButtonSubscribe(false);
                                documentId = "";
                                unsuscribeFromTopic();
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
            btnSubscribe.setBackgroundColor(getResources().getColor(R.color.primary_dark));
            mSubscribed = true;
        } else {
            btnSubscribe.setText("Suscribirse");
            btnSubscribe.setBackgroundColor(getResources().getColor(R.color.accent));
            mSubscribed = false;
        }

        btnSubscribe.setEnabled(true);
    }

    public void checkFollow() {
        btnSubscribe.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("premiumUsersSubscriptions")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("storeName", premiumId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        //read subscription id
                        documentId = task.getResult().getDocuments().get(0).getId();
                        mSubscribed = true;
                    }
                    setupButtonSubscribe(!task.getResult().isEmpty());
                }
            }
        });
    }
  
    public void initElements(View rootView) {
        profilePhoto = rootView.findViewById(R.id.premium_profile_photo);
        btnSubscribe = rootView.findViewById(R.id.btn_subscribe);
        txtEmail = rootView.findViewById(R.id.txtEmail);
        txtLocalization = rootView.findViewById(R.id.txtLocalization);
        lnlface = rootView.findViewById(R.id.lnlFace);
        lnlInsta = rootView.findViewById(R.id.lnlInta);
        lblDate = rootView.findViewById(R.id.lblDate);

        Bundle args = getArguments();
        if (args != null) {
            txtLocalization.setText(args.getString("location"));
            setProfilePhoto(args.getString("photo"));
            txtEmail.setText(args.getString("email"));
            userName=args.getString("name");
            setOnClickFacebook(args.getString("facebook"));
            setOnClickInstagram(args.getString("instagram"));
        }
    }

    private void displayMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
