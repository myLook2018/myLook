package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.mylook.mylook.R;

import static android.app.Activity.RESULT_OK;

public class OutfitsTab extends Fragment implements OutfitListAdapter.OutfitClickListener,
        OutfitListAdapter.OutfitLongClickListener {

    static final int OUTFIT_INFO_REQUEST = 1;

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
        closet.getOutfits().observe(this, outfits -> {
            adapter = new OutfitListAdapter(getActivity(), outfits);
            adapter.setClickListener(this);
            adapter.setLongClickListener(this);
            outfitsRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        });
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
        addOutfit.setOnClickListener(v -> createInputDialog());
    }

    //TODO despues de seleccionar las prendas (en la actividad que se encargue)
    public void createInputDialog() {

    }

    @Override
    public void onOutfitClick(View view, int position) {
        showOutfit(position);
    }

    @Override
    public boolean onOutfitLongClick(View view, int position) {
        return outfitOptions(position);
    }

    private boolean outfitOptions(int position) {
        String[] options = {"Ver", "Editar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Conjunto");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showOutfit(position);
                    break;
                case 1:
                    editOutfit(position);
                    break;
                case 2:
                    deleteOutfit(position);
                    break;
                default:
                    break;
            }
        });
        builder.show();
        return true;
    }

    private void showOutfit(int position) {
        startActivityForResult(new Intent(getContext(), OutfitInfoActivity.class)
                .putExtra("outfit", adapter.getItem(position)),
                OUTFIT_INFO_REQUEST);
    }

    private void editOutfit(int position) {
        /*AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
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
                    }
                }).create().show();
        Intent intent = new Intent(getActivity(), CreateOutfitActivity.class);
        intent.putExtra("name", input.getText().toString());
        intent.putExtra("category", "normal");
        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);*/
    }

    private void deleteOutfit(int position) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OUTFIT_INFO_REQUEST) {
            if (resultCode == RESULT_OK) {

            }
        }
    }
}
