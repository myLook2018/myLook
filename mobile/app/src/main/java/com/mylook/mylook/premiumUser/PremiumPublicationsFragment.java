package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumPublication;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class PremiumPublicationsFragment extends Fragment {

    private FirebaseFirestore dB=null;
    private  String premiumUserId;
    private static String coverPh;

    public PremiumPublicationsFragment() {
    }

    @SuppressLint("ValidFragment")
    public PremiumPublicationsFragment(String premiumUserId) {
        dB = FirebaseFirestore.getInstance();
        this.premiumUserId=premiumUserId;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_shopwindow, container, false);

        // Obtención del grid view
        GridViewWithHeaderAndFooter grid = rootView.findViewById(R.id.gridview);
        // Inicializar el grid view
        setupPublicationsGridView(grid);
        return rootView;
    }

    private void setupPublicationsGridView(final GridViewWithHeaderAndFooter grid) {

        Log.d("Store Catalog gridView", "setupGridView: Setting up store grid.");
        final ArrayList<PremiumPublication> publications = new ArrayList<>();
        final String[] documentID = new String[1];
        dB.collection("premiumPublications").whereEqualTo("userId", premiumUserId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            for (DocumentSnapshot doc:task.getResult().getDocuments()){
                                documentID[0] = task.getResult().getDocuments().get(0).getId();
                                PremiumPublication pub=doc.toObject(PremiumPublication.class);
                                publications.add(pub);
                            }
                            grid.setAdapter(new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,publications,0));
                        } else {
                            if(task.getException()!=null)
                            Log.e("Firestore task", "onComplete: " + task.getException());
                            else
                                Log.e("Firestore task", "onComplete: No existe vidriera");

                        }
                    }
                });

    }

}


