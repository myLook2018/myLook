package com.mylook.mylook.storeProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
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

public class StoreInfoFragment extends Fragment {

    private Button btnSubscribe;
    private boolean mSubscribed;
    private String subscriptionDocument;

    public StoreInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscribed = false;
        checkFollow();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView storePhoto = view.findViewById(R.id.premium_profile_photo);
        Glide.with(this).load(getArguments().getString("photo")).into(storePhoto);

        TextView txtStoreName = view.findViewById(R.id.profile_store_name);
        txtStoreName.setText(getArguments().getString("name"));

        TextView txtDescription = view.findViewById(R.id.txtDescription);
        txtDescription.setText(getArguments().getString("description"));
        txtDescription.setMovementMethod(new ScrollingMovementMethod());

        Button btnMoreInfo = view.findViewById(R.id.btnMasInfo);
        btnMoreInfo.setOnClickListener(v -> ((StoreActivity) getActivity()).moreInfo());

        btnSubscribe = view.findViewById(R.id.btn_subscribe);
        setOnClickSubscribe();

        super.onViewCreated(view, savedInstanceState);
    }

    public void setOnClickSubscribe() {
        btnSubscribe.setOnClickListener(view -> {
            btnSubscribe.setEnabled(false);
            if (!mSubscribed) {
                Subscription newSubscription = new Subscription(
                        getArguments().getString("name"),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
                FirebaseFirestore.getInstance().collection("subscriptions")
                        .add(newSubscription)
                        .addOnSuccessListener(document -> {
                            setupButtonSuccessfulSubscribe(true);
                            displayMessage("Ahora estás suscripto a " +
                                    getArguments().getString("name"));
                        });
            } else {
                FirebaseFirestore.getInstance().collection("subscriptions")
                        .document(subscriptionDocument).delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                setupButtonSuccessfulSubscribe(false);
                                displayMessage("Ya no estás suscripto");
                            }
                        });
            }
        });
    }

    private void setupButtonSuccessfulSubscribe(boolean subscribed) {
        //TODO toggle button seria mejor para manejar el estado y la vista
        if (subscribed) {
            btnSubscribe.setText("Desuscribirse");
            btnSubscribe.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.primary_dark));
            mSubscribed = true;
        } else {
            btnSubscribe.setText("Suscribirse");
            btnSubscribe.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.accent));
            mSubscribed = false;
        }
        btnSubscribe.setEnabled(true);
    }

    public void checkFollow() {
        FirebaseFirestore.getInstance().collection("subscriptions")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("storeName", getArguments().getString("name"))
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            subscriptionDocument = task.getResult().getDocuments().get(0).getId();
                            mSubscribed = true;
                        }
                        setupButtonSuccessfulSubscribe(mSubscribed);
                    }
                });
    }

    private void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
