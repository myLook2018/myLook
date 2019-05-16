package com.mylook.mylook.storeProfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

@SuppressLint("ValidFragment")
public class StoreInfoFragment extends Fragment {

    private  FirebaseUser user;
    private FirebaseFirestore dB;
    private ImageView storePhoto;
    private Button btnSubscribe,btnMoreInfo;
    private TextView storeName;
    private TextView txtDescription;
    private String storeNameString;
    private Context context;
    private boolean mSubscribed;
    private String documentId="";


    public StoreInfoFragment(Context context,String storeName) {
        dB = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storeNameString=storeName;
        this.context=context;
        checkFollow();

    }

    public StoreInfoFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_store, container, false);
        initElements(rootView);

        setOnClickSubscribe();
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreActivity.moreInfo();
            }
        });

        return rootView;
    }

    public void setStorePhoto(String storePhoto) {
        Glide.with(context).load(storePhoto).into(this.storePhoto);
    }


    public void setStoreName(String storeName) {
        this.storeName.setText(storeName);
    }

    public void setTxtDescription(String txtDescription) {
        this.txtDescription.setText(txtDescription);
    }
    public void setOnClickSubscribe(){
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubscribe.setEnabled(false);
                if (!mSubscribed) {

                    Subscription newSubscription = new Subscription(storeNameString, user.getUid());

                    dB.collection("subscriptions").add(newSubscription).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Firestore task", "DocumentSnapshot written with ID: " + documentReference.getId());
                            documentId = documentReference.getId();
                            setupButtonSubscribe(true);
                            displayMessage("Ahora estas suscripto a "+storeNameString);
                            Sesion.getInstance().updateActivitiesStatus(Sesion.HOME_FRAGMENT);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore task", "Error adding document", e);
                            btnSubscribe.setEnabled(true);
                        }
                    });

                } else {
                    dB.collection("subscriptions").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setupButtonSubscribe(false);
                                documentId = "";
                                Log.e("BUTTON",documentId);
                                displayMessage("Ya no estás suscripto");
                                Sesion.getInstance().updateActivitiesStatus(Sesion.HOME_FRAGMENT);
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
            mSubscribed=false;
        }

        btnSubscribe.setEnabled(true);
    }
    public void checkFollow() {

        dB.collection("subscriptions")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("storeName", storeNameString)
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
        storePhoto = (ImageView) rootView.findViewById(R.id.premium_profile_photo);
        btnSubscribe =rootView.findViewById(R.id.btn_subscribe);
        storeName = (TextView) rootView.findViewById(R.id.profile_store_name);
        txtDescription=rootView.findViewById(R.id.txtDescription);
        txtDescription.setMovementMethod(new ScrollingMovementMethod());
        btnMoreInfo=rootView.findViewById(R.id.btnMasInfo);
    }

    private void displayMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
