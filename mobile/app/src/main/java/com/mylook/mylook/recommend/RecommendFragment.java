package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;
import com.mylook.mylook.session.Sesion;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends Fragment {

    private Context mContext;
    private FirebaseFirestore dB;
    private static List<RequestRecommendation> requestRecommendationsList;
    public final static String TAG = "RecommendFragment";
    public static int NEW_REQUEST = 1;
    public static int RESULT_OK = 1;
    RequestRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private static RecommendFragment homeInstance = null;

    public static RecommendFragment getInstance() {
        if (homeInstance == null) {
            homeInstance = new RecommendFragment();
        }
        return homeInstance;
    }

    public static void refreshStatus() {
        if (homeInstance != null) {
            requestRecommendationsList = new ArrayList<>();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
        this.dB = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRecommend);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        progressBar = view.findViewById(R.id.progressBar);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(mContext, RecommendActivityAddDesc.class);
            startActivityForResult(intent, NEW_REQUEST);
        });
        if (requestRecommendationsList == null || requestRecommendationsList.size() == 0)
            requestRecommendationsList = new ArrayList<>();
        adapter = new RequestRecyclerViewAdapter(mContext, requestRecommendationsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Activity result", "Request " + requestCode + " result " + resultCode);
        if (requestCode == NEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                initRecyclerView();
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        dB.collection("requestRecommendations")
                .whereEqualTo("userId", Sesion.getInstance().getSessionUserId()).orderBy("limitDate").get()
                .addOnCompleteListener(task -> {
                    requestRecommendationsList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            RequestRecommendation requestRecommendation = document.toObject(RequestRecommendation.class);
                            requestRecommendation.setDocumentId(document.getId());
                            Log.e("Request", requestRecommendation.getDocumentId());
                            requestRecommendationsList.add(requestRecommendation);
                        }
                        adapter.notifyDataSetChanged();

                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

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
                .setPositiveButton("Aceptar", (dialog1, which) ->
                        dialog1.cancel()).setMessage("Cuando no sepas que ponerte pedile a las tiendas que te ayuden a encontrar algo que te guste, apretá en" +
                        " el botón + para empezar")
                .create();
        alert.setOnShowListener(dialog12 ->
                alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple)));

        alert.show();
    }


}
