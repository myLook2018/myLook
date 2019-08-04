package com.mylook.mylook.storeProfile;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReputationFragment extends Fragment {

    private TextView lblCant;
    private MaterialRatingBar ratingBar;
    private TextView lblCantRecommendations;
    private TextView lblRecommendationsDescr;
    private float ratingSum = 0;
    private int recommendCount = 0;

    public ReputationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_reputation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView cardRecommendations = view.findViewById(R.id.cardRecommendations);
        cardRecommendations.setVisibility(View.VISIBLE);
        //TODO definir esto
        //TextView lblActiveStore = rootView.findViewById(R.id.lblActiveStore);
        TextView lblDate = view.findViewById(R.id.lblDate);
        lblDate.setText(getArguments().getString("registerDate"));
        ratingBar = view.findViewById(R.id.ratingBar);
        lblCantRecommendations = view.findViewById(R.id.lblCantRecommendations);
        lblRecommendationsDescr = view.findViewById(R.id.lblRecomendationsDescr);
        countRecommendations();
        lblCant = view.findViewById(R.id.lblCant);
        countSubscribers();
    }

    public void countRecommendations() {
        FirebaseFirestore.getInstance().collection("answeredRecommendations")
                .whereEqualTo("storeName", getArguments().getString("name"))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                //TODO que pasa si el usuario no pone feedback?
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (documentSnapshot.contains("feedBack")) {
                        ratingSum += Float.parseFloat((String) documentSnapshot.get("feedBack"));
                        recommendCount++;
                    }
                }
                if (ratingSum != 0 && recommendCount != 0) {
                    float prom = ratingSum / recommendCount;
                    ratingBar.setRating(prom);
                    ratingBar.setEnabled(false);
                    lblCantRecommendations.setText(String.format("Recomendó %d veces", recommendCount));
                    lblRecommendationsDescr.setText(typeRecommendation(prom));
                    recommendCount = 0;
                    ratingSum = 0;
                } else {
                    ratingBar.setVisibility(View.INVISIBLE);
                    lblCantRecommendations.setVisibility(View.INVISIBLE);
                    lblRecommendationsDescr.setText("Aún no realizó ninguna recomendación!");
                }
            }
        });
    }

    public void countSubscribers() {
        FirebaseFirestore.getInstance().collection("subscriptions")
                .whereEqualTo("storeName", getArguments().getString("name")).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int cant = task.getResult().getDocuments().size();
                        if (cant == 0)
                            lblCant.setText("Todavía no tiene suscriptores");
                        else
                            lblCant.setText(String.format("%d suscriptores!", cant));
                    }
                });
    }

    public String typeRecommendation(float prom) {
        if (prom >= 0 && prom <= 2) {
            return "No da buenas recomendaciones";
        } else if (prom > 2 && prom <= 3) {
            return "Sus recomendaciones son regulares";
        } else if (prom > 3 && prom < 4.5) {
            return "Sus recomendaciones son muy buenas!";
        } else return "Sus recomendaciones son excelentes!";
    }
}
