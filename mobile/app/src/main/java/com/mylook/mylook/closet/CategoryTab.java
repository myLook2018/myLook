package com.mylook.mylook.closet;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMuxer;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.OutfitAdapter;

import java.util.ArrayList;

public class CategoryTab extends Fragment {

    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Closet closet;
    private ArrayList<Outfit> outfits;
    private Context context;
    private GridView outfitGrid;
    private Activity act;
    private String dbUserId;
    private FloatingActionButton addOutfit;
    private ProgressBar mProgressBar;
    private static CategoryTab instance = null;
    private boolean loaded = false;
    private OutfitAdapter adapter;

    public CategoryTab() {
        // Required empty public constructor
    }

    public static CategoryTab getInstance() {
        if (instance == null)
            instance = new CategoryTab();
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        outfits = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    private void getUserId() {
        dB.collection("clients").whereEqualTo("userId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                    dbUserId = task.getResult().getDocuments().get(0).get("userId").toString();
                    getOutfits();
                } else {
                    dB.collection("clients").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                dbUserId = task.getResult().getDocuments().get(0).get("userId").toString();
                                getOutfits();
                            }
                        }
                    });
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();

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
            adapter = new com.mylook.mylook.utils.OutfitAdapter(act, outfits);
            setGridview();
            getUserId();
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

    private void setGridview() {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getOutfits() {
        dB.collection("closets")
                .whereEqualTo("userID", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                closet = document.toObject(Closet.class);
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
                                                    return;
                                                } else
                                                    Log.e("FAVORITES", "Nuuuuuuuuuuuuuuuuuuuuuu");
                                            }
                                        });
                            }
                        } else {
                            Log.e("FAVORITES", "NOOOOOOOOOOOOO");
                        }
                    }
                });
    }


    public void createInputDialog() {

        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint((CharSequence) "Nombre");


        LinearLayout linearLayout = new LinearLayout(getContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        input.setHint("Nombre");
        input.setLayoutParams(layoutParams);

        linearLayout.addView(input);
        linearLayout.setPadding(60, 20, 60, 20);

        dialog.setView(linearLayout);

        final android.app.AlertDialog alert = dialog.setTitle("Elegí un nombre para tu conjunto")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        String newOutfitName = input.getText().toString();
                        Intent intent = new Intent(getActivity(), OutfitActivity.class);
                        intent.putExtra("name", newOutfitName);
                        intent.putExtra("category", "normal");
                        startActivity(intent);
                    }

                }).create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));

            }
        });

        alert.show();
    }
}
