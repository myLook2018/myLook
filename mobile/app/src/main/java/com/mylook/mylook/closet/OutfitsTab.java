package com.mylook.mylook.closet;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mylook.mylook.R;

import static com.mylook.mylook.closet.OutfitCreateEditActivity.OUTFIT_CREATED;
import static com.mylook.mylook.closet.OutfitCreateEditActivity.OUTFIT_CREATE_REQUEST;
import static com.mylook.mylook.closet.OutfitCreateEditActivity.OUTFIT_EDIT_REQUEST;
import static com.mylook.mylook.closet.OutfitInfoActivity.OUTFIT_DELETED;
import static com.mylook.mylook.closet.OutfitInfoActivity.OUTFIT_EDITED;
import static com.mylook.mylook.closet.OutfitInfoActivity.OUTFIT_INFO_REQUEST;

public class OutfitsTab extends Fragment implements OutfitListAdapter.OutfitClickListener {

    private RecyclerView outfitsRecyclerView;
    private OutfitListAdapter adapter;
    private ClosetModel closet;
    private ProgressBar mProgressBar;

    public OutfitsTab() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closet = ViewModelProviders.of(getParentFragment()).get(ClosetModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab_categories, container, false);
        outfitsRecyclerView = rootView.findViewById(R.id.recyclerview_outfits);
        outfitsRecyclerView.setHasFixedSize(true);
        outfitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.progressbar_outfits);
        mProgressBar.setVisibility(View.VISIBLE);
        FloatingActionButton addOutfit = view.findViewById(R.id.fab_add_outfit);
        addOutfit.setOnClickListener(v -> createOutfit());
        closet.getOutfits().observe(this, outfits -> {
            adapter = new OutfitListAdapter(getActivity(), outfits, this);
            outfitsRecyclerView.setAdapter(adapter);
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        });
    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOutfit(View v, int position) {
        startActivityForResult(new Intent(getContext(), OutfitInfoActivity.class)
                .putExtra("outfit", adapter.getItem(position)), OUTFIT_INFO_REQUEST);
    }

    @Override
    public void editOutfit(View v, int position) {
        startActivityForResult(new Intent(getContext(), OutfitCreateEditActivity.class)
                .putExtra("create", false)
                .putExtra("outfit", adapter.getItem(position)), OUTFIT_EDIT_REQUEST);
    }

    @Override
    public void deleteOutfit(View v, int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar conjunto")
                .setMessage("Estás seguro de que querés eliminar el conjunto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    closet.removeOutfit(position)
                            .addOnSuccessListener(task -> displayToast("Conjunto eliminado"))
                            .addOnFailureListener(task -> displayToast("Error al eliminar conjunto"));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void createOutfit() {
        startActivityForResult(new Intent(getContext(), OutfitCreateEditActivity.class)
                .putExtra("create", true), OUTFIT_CREATE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OUTFIT_INFO_REQUEST:
                if (resultCode == OUTFIT_EDITED) closet.reloadOutfits();
                if (resultCode == OUTFIT_DELETED) closet.reloadOutfits();
                break;
            case OUTFIT_CREATE_REQUEST:
                if (resultCode == OUTFIT_CREATED) closet.reloadOutfits();
                break;
            case OUTFIT_EDIT_REQUEST:
                if (resultCode == OUTFIT_EDITED) closet.reloadOutfits();
                break;
            //TODO agregar el caso de ArticleInfoActivity en el cual saco de favoritos,
            // paso por intent el id del articulo, onSuccess del delete hago reload
        }
    }


}