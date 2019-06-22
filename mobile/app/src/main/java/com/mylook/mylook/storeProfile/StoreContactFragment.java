package com.mylook.mylook.storeProfile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylook.mylook.R;

public class StoreContactFragment extends Fragment {

    private LinearLayout lnlFacebook, lnlInstagram, lnlTwitter;
    private TextView txtFacebook, txtTwitter, txtInstagram;

    public StoreContactFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView storeLocation = view.findViewById(R.id.store_location);
        storeLocation.setText(getArguments().getString("location"));
        TextView storePhone = view.findViewById(R.id.store_phone);
        storePhone.setText(getArguments().getString("phone"));
        txtFacebook = view.findViewById(R.id.txtFacebook);
        setOnClickFacebook(getArguments().getString("facebook"));
        txtTwitter = view.findViewById(R.id.txtTwitter);
        setOnClickTwitter(getArguments().getString("twitter"));
        txtInstagram = view.findViewById(R.id.txtInstagram);
        setOnClickInstagram(getArguments().getString("instagram"));
        lnlFacebook = view.findViewById(R.id.lnlFace);
        lnlInstagram = view.findViewById(R.id.lnlInta);
        lnlTwitter = view.findViewById(R.id.lnlTw);
    }

    //TODO puede simplificarse el codigo bastante aca con parametros

    public void setOnClickFacebook(String facebook) {
        txtFacebook.setText(facebook);
        if (!facebook.equals("")) {
            final String facebookURL = "https://www.facebook.com/" + facebook;
            txtFacebook.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookURL))
                            .setPackage("com.facebook.android"));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookURL)));
                }
            });
            lnlFacebook.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickTwitter(String twitter) {
        txtTwitter.setText(twitter);
        if (!twitter.equals("")) {
            final String twitterURL = "https://twitter.com/" + twitter;
            this.txtTwitter.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterURL))
                            .setPackage("com.twitter.android"));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterURL)));
                }

            });
            lnlTwitter.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickInstagram(String instagram) {
        txtTwitter.setText(instagram);
        if (!instagram.equals("")) {
            final String instagramURL = "https://www.instagram.com/" + instagram;
            this.txtInstagram.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instagramURL))
                            .setPackage("com.instagram.android"));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instagramURL)));
                }
            });
            lnlInstagram.setVisibility(View.VISIBLE);
        }
    }
}
