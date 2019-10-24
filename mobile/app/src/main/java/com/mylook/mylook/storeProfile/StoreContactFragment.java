package com.mylook.mylook.storeProfile;

import android.content.ActivityNotFoundException;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.session.Session;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreContactFragment extends Fragment {

    private LinearLayout lnlface,lnlInsta,lnlTw;
    private TextView storeLocation, storePhone;
    private TextView lblMaps;
    private CircleImageView storePhoto;
    private Button btnSubscribe;
    private boolean mSubscribed;
    private String subscriptionDocument;


    public StoreContactFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_store, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView){
        Bundle args = getArguments();

        if (args != null) {
            String store = args.getString("name");

            lblMaps = rootView.findViewById(R.id.txtMaps);
            setOnClickLocation(args.getString("storeName"), args.getDouble("latitude"), args.getDouble("longitude"));

            storeLocation = rootView.findViewById(R.id.store_location);
            setStoreLocation(args.getString("location"));

            storePhone = rootView.findViewById(R.id.store_phone);
            setStorePhone(args.getString("phone"));

            lnlface=rootView.findViewById(R.id.lnlFace);
            setOnClickFacebook(args.getString("facebook"));

            lnlTw=rootView.findViewById(R.id.lnlTw);
            setOnClickTwitter(args.getString("twitter"));

            lnlInsta=rootView.findViewById(R.id.lnlInta);
            setOnClickInstagram(args.getString("instagram"));

            storePhoto = rootView.findViewById(R.id.premium_profile_photo);
            setStorePhoto(args.getString("photo"));

            btnSubscribe = rootView.findViewById(R.id.btn_subscribe);
            checkFollow(store);
            setOnClickSubscribe(store);
        }

    }

    private void setStoreLocation(String storeLocation) {
        this.storeLocation.setText(storeLocation);
    }

    private void setStorePhone(String storePhone) {
        this.storePhone.setText(storePhone);
    }
    private void setStorePhoto(String storePhoto) {
        Glide.with(getContext()).load(storePhoto).into(this.storePhoto);
    }

    private void setOnClickFacebook(String txtFacebook) {
        if (!Strings.isNullOrEmpty(txtFacebook)) {
            final String finalTxtFacebook = "https://www.facebook.com/"+txtFacebook;
            this.lnlface.setOnClickListener(v -> {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook));
                intent.setPackage("com.facebook.android");
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtFacebook)));
                }
            });
            lnlface.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickTwitter(String txtTwitter) {
        if (!Strings.isNullOrEmpty(txtTwitter)) {
            final String finalTxtTwitter = "https://twitter.com/"+txtTwitter;
            this.lnlTw.setOnClickListener(v -> {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtTwitter));
                intent.setPackage("com.twitter.android");
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtTwitter)));
                }

            });
            lnlTw.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickInstagram(String txtInstagram) {
        if (!Strings.isNullOrEmpty(txtInstagram)) {
            final String finalTxtInstagram = "https://www.instagram.com/"+txtInstagram;
            this.lnlInsta.setOnClickListener(v -> {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram));
                intent.setPackage("com.instagram.android");
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtInstagram)));
                }
            });
            lnlInsta.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickLocation(String storeName, Double storeLatitude, Double storeLongitude) {
        String latitude = storeLatitude.toString();
        String longitude = storeLongitude.toString();
        Log.e("CLICK LOCATION", String.format("Latitud: %s , Longitud: %s", latitude, longitude));
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?z=14&q=" + latitude + "," + longitude + "(" + storeName + ")");
        Log.e("Location uri: ", gmmIntentUri.toString());
        this.lblMaps.setOnClickListener(v -> {
            Log.d("CLICK LOCATION", "Se hizo click en la ubicacion de la tienda " + storeName);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                Log.e("Error al abrir el link a Maps. El error es: " , e.getMessage());
                startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
            }
        });
    }

    private void checkFollow(String storeName) {
        btnSubscribe.setEnabled(false);
        try {
            FirebaseFirestore.getInstance().collection("subscriptions")
                    .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("storeName", storeName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            subscriptionDocument = task.getResult().getDocuments().get(0).getId();
                            mSubscribed = true;
                        }
                        setupButtonSubscribe(mSubscribed);
                        btnSubscribe.setEnabled(true);
                    });
        }catch (Exception e)
        {
            Log.e("STORE INFO", "Error en el checkeo del follow");
        }
    }

    private void setOnClickSubscribe(String storeName) {
        btnSubscribe.setOnClickListener(view -> {
            btnSubscribe.setEnabled(false);
            if (!mSubscribed) {
                Subscription newSubscription = new Subscription(storeName, FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseFirestore.getInstance().collection("subscriptions").add(newSubscription).addOnSuccessListener(documentReference -> {
                    Log.d("Firestore task", "DocumentSnapshot written with ID: " + documentReference.getId());
                    subscriptionDocument = documentReference.getId();
                    setupButtonSubscribe(true);
                    btnSubscribe.setEnabled(true);
                    displayMessage("Ahora estás suscripto a " + storeName);
                    Session.getInstance().updateActivitiesStatus(Session.HOME_FRAGMENT);
                }).addOnFailureListener(e -> {
                    Log.w("Firestore task", "Error adding document", e);
                    btnSubscribe.setEnabled(true);
                });
            } else {
                FirebaseFirestore.getInstance().collection("subscriptions").document(subscriptionDocument).delete().addOnSuccessListener(task -> {
                    mSubscribed = false;
                    setupButtonSubscribe(false);
                    btnSubscribe.setEnabled(true);
                    subscriptionDocument = null;
                    displayMessage("Ya no estás suscripto a " + storeName);
                    Session.getInstance().updateActivitiesStatus(Session.HOME_FRAGMENT);
                    btnSubscribe.setEnabled(true);
                });
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
    }

    private void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
