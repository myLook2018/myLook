package com.mylook.mylook.storeProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.session.Session;

public class StoreInfoFragment extends Fragment {

    private ImageView storePhoto;
    private Button btnSubscribe;
    private TextView storeName;
    private TextView txtDescription;
    private boolean mSubscribed;
    private String subscriptionDocument;

    public StoreInfoFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_store, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Bundle args = getArguments();

        if (args != null) {
            String store = args.getString("storeName");

            storeName = rootView.findViewById(R.id.profile_store_name);
            setStoreName(store);

            storePhoto = rootView.findViewById(R.id.premium_profile_photo);
            setStorePhoto(args.getString("photo"));

            txtDescription = rootView.findViewById(R.id.txtDescription);
            txtDescription.setMovementMethod(new ScrollingMovementMethod());
            setStoreDescription(args.getString("description"));

            Button btnMoreInfo = rootView.findViewById(R.id.btnMasInfo);
            btnMoreInfo.setOnClickListener(v -> ((StoreActivity) getActivity()).moreInfo());

            btnSubscribe = rootView.findViewById(R.id.btn_subscribe);
            checkFollow(store);
            setOnClickSubscribe(store);
        }
    }

    private void setStoreName(String storeName) {
        this.storeName.setText(storeName);
    }

    private void setStorePhoto(String storePhoto) {
        Glide.with(getContext()).load(storePhoto).into(this.storePhoto);
    }

    private void setStoreDescription(String txtDescription) {
        this.txtDescription.setText(txtDescription);
    }

    private void checkFollow(String storeName) {
        btnSubscribe.setEnabled(false);
        try {
            //Me fijo en base si ya existe en suscripciones el actual usuario
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
        } catch (Exception e) {
            System.out.println("Error en el checkeo del follow. El error es: " + e.getMessage());
        }
    }

    private void setOnClickSubscribe(String storeName) {
        btnSubscribe.setOnClickListener(view -> {
            btnSubscribe.setEnabled(false);
            if (!mSubscribed) {
                //Si no esta subscripto se crea la subscripcion en bd
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
