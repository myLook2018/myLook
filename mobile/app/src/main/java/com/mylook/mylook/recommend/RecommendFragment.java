package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;
import com.mylook.mylook.session.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecommendFragment extends Fragment {

    private FloatingActionButton fab;
    private Context mContext;
    private RecyclerView recyclerView;
    private static List<RequestRecommendation> requestRecommendationsList;
    public final static String TAG = "RecommendFragment";
    public static int NEW_REQUEST = 1;
    public static int RESULT_OK=1;
    private static RecommendFragment homeInstance = null;
    RequestRecyclerViewAdapter adapter;

     private ProgressBar progressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "OnViewCreated- savedInstance" + savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
        recyclerView = view.findViewById(R.id.recyclerViewRecommend);
        fab = view.findViewById(R.id.fab);
        progressBar = view.findViewById(R.id.progressBar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RecommendActivityAddDesc.class);
                startActivityForResult(intent, NEW_REQUEST);
            }
        });
        if(requestRecommendationsList == null || requestRecommendationsList .size() == 0)
            requestRecommendationsList = new ArrayList<RequestRecommendation>();
        adapter = new RequestRecyclerViewAdapter(mContext, requestRecommendationsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static RecommendFragment getInstance() {
        if (homeInstance == null) {
            homeInstance = new RecommendFragment();
        }
        return homeInstance;
    }

    /**
     * Método para cuando haya ocrurrido algun cambio y haya que actualizar los objetos
     */
    public void refreshStatus(){
        if(homeInstance!=null){
            initRecyclerView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Activity result", "Reuest "+requestCode+" result "+resultCode);
        if(requestCode == NEW_REQUEST){
            if(resultCode == RESULT_OK){
                initRecyclerView();
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "OnCreate - savedInstance" + savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "OnCreateView- savedInstance" + savedInstanceState);

        return inflater.inflate(R.layout.fragment_recommend, null);
    }


    private void initRecyclerView() {
        getRequestRecommendations();

    }

    @Override
    public void onResume() {
        Log.e(TAG, "On Resume");
        super.onResume();
        if (requestRecommendationsList == null || requestRecommendationsList.size() == 0)
            initRecyclerView();
    }

    public void getRequestRecommendations() {
        progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "getRequestRecommendations");
        FirebaseFirestore.getInstance().collection("requestRecommendations")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("isClosed", Query.Direction.ASCENDING)
                .orderBy("limitDate").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        requestRecommendationsList.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RequestRecommendation requestRecommendation = document.toObject(RequestRecommendation.class);
                                requestRecommendation.setDocumentId(document.getId());
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(requestRecommendation.getLimitDate());
                                Calendar calNow = Calendar.getInstance();
                                int daysDiff = cal.get(Calendar.DAY_OF_YEAR) - calNow.get(Calendar.DAY_OF_YEAR);
                                Log.e("Days diff", ""+daysDiff);
                                if ( (!requestRecommendation.getIsClosed() && cal.getTimeInMillis() > calNow.getTimeInMillis())
                                        || ( daysDiff >= -7)) {
                                    requestRecommendationsList.add(requestRecommendation);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.closet_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.help_menu) {
            createHelpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createHelpDialog() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this.getContext(), R.style.AlertDialogTheme);
        final android.app.AlertDialog alert = dialog.setTitle("Ayuda")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setMessage("Cuando no sepas que ponerte pedile a las tiendas que te ayuden a encontrar algo que te guste, apretá en" +
                        " el botón + para empezar")
                .create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
            }
        });

        alert.show();
    }


}
