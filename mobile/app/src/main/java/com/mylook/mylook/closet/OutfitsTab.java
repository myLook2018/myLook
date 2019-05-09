package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.session.Sesion;
import com.mylook.mylook.utils.OutfitAdapter;

import java.util.ArrayList;

public class OutfitsTab extends Fragment {

    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Outfit> outfits;
    private GridView outfitGrid;
    private String dbUserId = Sesion.getInstance().getSessionUserId();
    private FloatingActionButton addOutfit;
    private ProgressBar mProgressBar;
    private static OutfitsTab instance = null;
    private static boolean loaded = false;
    private OutfitAdapter adapter;

    public OutfitsTab() {
        // Required empty public constructor
    }

    public static OutfitsTab getInstance() {
        if (instance == null)
            instance = new OutfitsTab();
        return instance;
    }

    /**
     * Método para cuando haya habido algun cambio y haya que actualizar los objetos
     */
    public static void refreshStatus(){
        if(instance!=null){
            loaded = false;
        }
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
        addOutfit = view.findViewById(R.id.addOutfit);
        if (!loaded) {
            mProgressBar.setVisibility(View.VISIBLE);
            outfits = new ArrayList<>();
            adapter = new OutfitAdapter(getActivity(), outfits);
            setGridView();
            getOutfits();
        }


        outfitGrid.setAdapter(adapter);
        addOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createInputDialog();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void setGridView() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 2;
        outfitGrid.setColumnWidth(imageWidth);
        outfitGrid.setHorizontalSpacing(8);
        outfitGrid.setNumColumns(2);
        outfitGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                loadOutfit(parent, position);
            }
        });
    }

    private void loadOutfit(AdapterView<?> parent, int position) {
        mProgressBar.setVisibility(View.VISIBLE);
        final String outfitId = ((Outfit) parent.getAdapter().getItem(position)).getOutfitId();
        dB.collection("closets").whereEqualTo("userID", dbUserId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final String id = task.getResult().getDocuments().get(0).getId();
                            dB.collection("closets").document(id).collection("outfits")
                                    .document(outfitId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "No se ha podido cargar tus favoritos", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getOutfits() {
        dB.collection("outfits")
                .whereEqualTo("userID", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                dB.collection("closets").document(id).collection("outfits").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    ArrayList<String> arrayList = new ArrayList<>();
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        Outfit outfit = documentSnapshot.toObject(Outfit.class);
                                                        outfit.setOutfitId(documentSnapshot.getId());
                                                        outfits.add(outfit);
                                                        adapter.notifyDataSetChanged();
                                                        loaded = true;
                                                    }
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }


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
                        Intent intent = new Intent(getActivity(), OutfitActivity.class);
                        intent.putExtra("name", input.getText().toString());
                        intent.putExtra("category", "normal");
                        intent.putExtra("userID", dbUserId);
                        startActivity(intent);
                    }

                }).create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));

            }
        });
        alert.show();
    }
}
