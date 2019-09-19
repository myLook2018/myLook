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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.mylook.mylook.R;

public class StoreContactFragment extends Fragment {

    private LinearLayout lnlface, lnlInsta, lnlTw;
    private TextView storeLocation, storePhone, txtTwitter, txtInstagram, txtFacebook;
    private Button btnReturnStoreInfo;

    public StoreContactFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_store, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Bundle args = getArguments();

        if (args != null) {
            storeLocation = rootView.findViewById(R.id.store_location);
            setOnClickLocation(args.getString("storeName"), args.getDouble("latitude"), args.getDouble("longitude"));

            storePhone = rootView.findViewById(R.id.store_phone);
            setStorePhone(args.getString("phone"));

            lnlface = rootView.findViewById(R.id.lnlFace);
            txtFacebook = rootView.findViewById(R.id.txtFacebook);
            setOnClickFacebook(args.getString("facebook"));

            lnlTw = rootView.findViewById(R.id.lnlTw);
            txtTwitter = rootView.findViewById(R.id.txtTwitter);
            setOnClickTwitter(args.getString("twitter"));

            lnlInsta = rootView.findViewById(R.id.lnlInsta);
            txtInstagram = rootView.findViewById(R.id.txtInstagram);
            setOnClickInstagram(args.getString("instagram"));
        }

        btnReturnStoreInfo = rootView.findViewById(R.id.btnReturnStoreInfo);
        btnReturnStoreInfo.setOnClickListener(v -> ((StoreActivity) getActivity()).returnToStoreInfo());
    }

    private void setOnClickLocation(String storeName, Double storeLatitude, Double storeLongitude) {
        String latitude = storeLatitude.toString();
        String longitude = storeLongitude.toString();
        Log.d("CLICK LOCATION", String.format("Latitud: %s , Longitud: %s", latitude, longitude));
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?z=14&q=" + latitude + "," + longitude + "(" + storeName + ")");
        System.out.println("Location uri: " + gmmIntentUri.toString());
        this.storeLocation.setOnClickListener(v -> {
            Log.d("CLICK LOCATION", "Se hizo click en la ubicacion de la tienda " + storeName);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                System.out.println("Error al abrir el link a Maps. El error es: " + e.getMessage());
                startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
            }
        });
    }

    private void setStorePhone(String storePhone) {
        this.storePhone.setText(storePhone);
    }

    private void setOnClickFacebook(String txtFacebook) {
        if (!Strings.isNullOrEmpty(txtFacebook)) {
            final String finalTxtFacebook = "https://www.facebook.com/" + txtFacebook;
            this.txtFacebook.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook));
                intent.setPackage("com.facebook.android");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    System.out.println("Error al abrir el link a Facebook. El error es: " + e.getMessage());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook)));
                }
            });
            lnlface.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickTwitter(String txtTwitter) {
        if (!Strings.isNullOrEmpty(txtTwitter)) {
            final String finalTxtTwitter = "https://twitter.com/" + txtTwitter;
            this.txtTwitter.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtTwitter));
                intent.setPackage("com.twitter.android");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    System.out.println("Error al abrir el link a Twitter. El error es: " + e.getMessage());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtTwitter)));
                }

            });
            lnlTw.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickInstagram(String txtInstagram) {
        if (!Strings.isNullOrEmpty(txtInstagram)) {
            final String finalTxtInstagram = "https://www.instagram.com/" + txtInstagram;
            this.txtInstagram.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram));
                intent.setPackage("com.instagram.android");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    System.out.println("Error al abrir el link a Instagram. El error es: " + e.getMessage());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram)));
                }
            });
            lnlInsta.setVisibility(View.VISIBLE);
        }
    }
}
