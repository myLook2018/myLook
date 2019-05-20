package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.utils.OutfitAdapter;

import java.util.ArrayList;

public class OutfitsTab extends Fragment {

    private GridView outfitGrid;
    private ProgressBar mProgressBar;
    private OutfitAdapter adapter;
    private FavoritesViewModel favModel;

    public OutfitsTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgressBar = view.findViewById(R.id.mProgressBar);

        outfitGrid = view.findViewById(R.id.grid_colecciones);
        FloatingActionButton addOutfit = view.findViewById(R.id.addOutfit);
        mProgressBar.setVisibility(View.VISIBLE);
        //TODO set adapter
        adapter = new OutfitAdapter(getActivity(), null);
        setGridView();
        getOutfits();

        outfitGrid.setAdapter(adapter);
        addOutfit.setOnClickListener(v -> createInputDialog());
        super.onViewCreated(view, savedInstanceState);
    }

    private void setGridView() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 2;
        outfitGrid.setColumnWidth(imageWidth);
        outfitGrid.setHorizontalSpacing(8);
        outfitGrid.setNumColumns(2);
        outfitGrid.setOnItemClickListener((parent, v, position, id) -> loadOutfit(parent, position));
    }

    /* TODO seccion de codigo para intent a viewoutfit
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Outfit outfit = task.getResult().toObject(Outfit.class);
                                Intent intent = new Intent(getContext(), ViewOutfitActivity.class);
                                intent.putExtra("items", outfit.getItems());
                                intent.putExtra("name", outfit.getName());
                                intent.putExtra("category", outfit.getCategory());
                                intent.putExtra("id", task.getResult().getId());
                                mProgressBar.setVisibility(View.INVISIBLE);
                                startActivity(intent);
                                */

    public void createInputDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        final EditText input = new EditText(getContext());
        final LinearLayout linearLayout = new LinearLayout(getContext());
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nombre");
        input.setLayoutParams(layoutParams);
        linearLayout.addView(input);
        linearLayout.setPadding(60, 20, 60, 20);
        layoutParams.gravity = Gravity.CENTER;
        dialog.setView(linearLayout);

        final AlertDialog alert = dialog.setTitle("Elegí un nombre para tu conjunto")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if ("".equals(input.getText().toString())) {
                            Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(getActivity(), CreateOutfitActivity.class);
                        intent.putExtra("name", input.getText().toString());
                        intent.putExtra("category", "normal");
                        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                    }

                }).create();
        alert.setOnShowListener(dialog1 -> alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(R.color.purple, )));
        alert.show();
    }
}
