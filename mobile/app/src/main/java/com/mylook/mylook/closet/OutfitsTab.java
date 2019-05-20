package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.utils.OutfitAdapter;

import java.util.ArrayList;

public class OutfitsTab extends Fragment {

    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Outfit> outfits;
    private GridView outfitGrid;
    private ProgressBar mProgressBar;
    private OutfitAdapter adapter;

    public OutfitsTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        outfits = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgressBar = view.findViewById(R.id.mProgressBar);
        outfitGrid = view.findViewById(R.id.grid_colecciones);
        FloatingActionButton addOutfit = view.findViewById(R.id.addOutfit);
        mProgressBar.setVisibility(View.VISIBLE);
        outfits = new ArrayList<>();
        adapter = new OutfitAdapter(getActivity(), outfits);
        setGridView();

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

    private void loadOutfit(AdapterView<?> parent, int position) {
        mProgressBar.setVisibility(View.VISIBLE);
        final String outfitId = ((Outfit) parent.getAdapter().getItem(position)).getOutfitId();
        dB.collection("closets").whereEqualTo("userID",
                FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final String id = task.getResult().getDocuments().get(0).getId();
                        dB.collection("closets").document(id).collection("outfits")
                                .document(outfitId).get().addOnCompleteListener(task1 -> {
                            Outfit outfit = task1.getResult().toObject(Outfit.class);
                            Intent intent = new Intent(getContext(), ViewOutfitActivity.class);
                            intent.putExtra("items", outfit.getItems());
                            intent.putExtra("name", outfit.getName());
                            intent.putExtra("category", outfit.getCategory());
                            intent.putExtra("id", task1.getResult().getId());
                            mProgressBar.setVisibility(View.INVISIBLE);
                            startActivity(intent);
                        });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "No se ha podido cargar tus favoritos", Toast.LENGTH_LONG).show());
    }

    public void createInputDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        EditText input = new EditText(getContext());
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nombre");
        input.setLayoutParams(layoutParams);
        linearLayout.addView(input);
        linearLayout.setPadding(60, 20, 60, 20);
        layoutParams.gravity = Gravity.CENTER;
        dialog.setView(linearLayout);
        dialog.setTitle("Elegí un nombre para tu conjunto")
                .setPositiveButton("Aceptar", (paramDialogInterface, paramInt) -> {
                    if ("".equals(input.getText().toString())) {
                        Toast.makeText(getContext(), "El nombre no puede estar vacío",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), CreateOutfitActivity.class);
                    intent.putExtra("name", input.getText().toString());
                    intent.putExtra("category", "normal");
                    intent.putExtra("userID",
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(intent);
                }).create().show();
    }
}
