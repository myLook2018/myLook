package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mylook.mylook.R;
import com.mylook.mylook.utils.OutfitListAdapter;


public class OutfitsTab extends Fragment {

    private RecyclerView outfitsRecyclerView;
    private OutfitListAdapter adapter;
    private ClosetModel closet;
    private ProgressBar mProgressBar;

    public OutfitsTab() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new OutfitListAdapter(getActivity(), null);
        closet = ViewModelProviders.of(getParentFragment()).get(ClosetModel.class);
        closet.getOutfits().observe(this, outfits -> {
            adapter.setOutfits(outfits);
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        outfitsRecyclerView = view.findViewById(R.id.recyclerview_outfits);
        FloatingActionButton addOutfit = view.findViewById(R.id.fab_add_outfit);
        //TODO set adapter
        setGridView();
        outfitsRecyclerView.setAdapter(adapter);
        addOutfit.setOnClickListener(v -> createInputDialog());
    }

    private void setGridView() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 2;
        outfitsRecyclerView.addOnItemTouchListener();
        outfitsRecyclerView.setOnItemClickListener((parent, v, position, id) -> {
            //TODO loadOutfit(parent, position);
        });
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

    //TODO despues de seleccionar las prendas (en la actividad que se encargue)
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

    public interface OnItemClickListener {
        void onItemClick()
    }

}
