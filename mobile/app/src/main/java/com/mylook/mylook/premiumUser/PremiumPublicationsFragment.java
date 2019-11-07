package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

    private  String premiumUserId;

    public PremiumPublicationsFragment() {
    }

    @SuppressLint("ValidFragment")
    public PremiumPublicationsFragment(String premiumUserId) {
        this.premiumUserId=premiumUserId;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_shopwindow, container, false);
        Bundle args =getArguments();

        if(args !=null){
            GridView grid = rootView.findViewById(R.id.gridview);
            setupPublicationsGridView(grid);
        }
        return rootView;
    }

    private void setupPublicationsGridView(final GridView grid) {

        Log.d("Store Catalog gridView", "setupGridView: Setting up store grid.");
        final ArrayList<PremiumPublication> publications = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("premiumPublications")
                .whereEqualTo("userId", premiumUserId)
                .get()
                .addOnSuccessListener(result ->{
                        if (!result.getDocuments().isEmpty()) {
                            for (DocumentSnapshot doc:result.getDocuments()){
                                PremiumPublication pub=doc.toObject(PremiumPublication.class);
                                publications.add(pub);
                            }
                            try{
                                grid.setAdapter(new GridImageAdapter(getActivity(),R.layout.ripple_image_view,publications,0));
                            }catch (Exception e){
                                Log.e("PremiumPubFragment","Exception: "+e.getMessage());
                            }
                        } })
                .addOnFailureListener(err -> Log.e("Firestore task", "onFailure: " + err));
    }

}


