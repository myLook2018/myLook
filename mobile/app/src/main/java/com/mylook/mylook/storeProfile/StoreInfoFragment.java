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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Subscription;
import com.mylook.mylook.session.Session;
import com.viewpagerindicator.CirclePageIndicator;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreInfoFragment extends Fragment {

    private TextView txtDescription;

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
            txtDescription = rootView.findViewById(R.id.txtDescription);
            txtDescription.setMovementMethod(new ScrollingMovementMethod());
            setStoreDescription(args.getString("description"));
        }
    }
    private void setStoreDescription(String txtDescription) {
        this.txtDescription.setText(txtDescription);
    }

}
