package com.mylook.mylook.storeProfile;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReputationFragment extends Fragment {

    private TextView lblDate;
    private TextView lblCant;
    private MaterialRatingBar ratingBar;
    private TextView lblCantRecommendations;
    private TextView lblRecommendationsDescr;
    private float ratingSum = 0;
    private int recommendCount = 0;
    private int promCount=0; // contador para el promedio

    public ReputationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_reputation, container, false);
        init(rootView);
        return rootView;
    }


    private void init(View rootView) {
        Bundle args = getArguments();

        if (args != null) {
            String store = args.getString("name");

            // TODO add condition
            TextView lblActiveStore = rootView.findViewById(R.id.lblActiveStore);

            lblDate = rootView.findViewById(R.id.lblDate);
            Date date=(Date) args.getSerializable("registerDate");
            if (date!=null) setRegisterDate(date);
            else lblDate.setText("Ale pasame este dato");

            lblCant = rootView.findViewById(R.id.lblCant);
            setSubscriptions(store);

            lblCantRecommendations = rootView.findViewById(R.id.lblCantRecommendations);
            lblRecommendationsDescr = rootView.findViewById(R.id.lblRecomendationsDescr);
            ratingBar = rootView.findViewById(R.id.ratingBar);
            setRecommendations(store);
        }
    }

    private void setRegisterDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        lblDate.setText(String.format("%s/%s/%s",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)));
    }

    private void setSubscriptions(String storeName) {
        FirebaseFirestore.getInstance().collection("subscriptions")
                .whereEqualTo("storeName", storeName)
                .get()
                .addOnSuccessListener(result -> {
                    int cant = result.getDocuments().size();
                    if (cant == 0)
                        lblCant.setText("No tiene suscriptores");
                    else if (cant == 1)
                        lblCant.setText("1 suscriptor");
                    else
                        lblCant.setText(String.format("%d suscriptores", cant));
                })
                .addOnFailureListener(err -> Log.e("ReputationFragment", "setSubscriptions: ", err));
    }

    private void setRecommendations(String storeName) {
        recommendCount = 0;
        ratingSum = 0;
        promCount=0;
        FirebaseFirestore.getInstance().collection("answeredRecommendations")
                .whereEqualTo("storeName", storeName)
                .get()
                .addOnSuccessListener(result -> {
                    for (DocumentSnapshot document : result.getDocuments()) {
                        if (document.contains("feedBack")) {
                            try {
                                if (document.get("feedBack") != null && document.get("feedBack") != ""
                                ) {
                                    ratingSum += Float.parseFloat((String) (document.get("feedBack")));
                                    promCount++;
                                }
                                recommendCount++;
                            } catch (NumberFormatException e) {
                                Log.e("ReputationFragment", "setRecommendations: ", e);
                            }
                        }
                    }
                    Log.e("ReputationFragment", "FINAL sum:" + ratingSum + " promCount " + promCount+" count "+recommendCount);


                    if (ratingSum != 0 && promCount != 0) {
                        float prom = ratingSum / promCount;
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setRating(prom);
                        ratingBar.setEnabled(false);
                        lblRecommendationsDescr.setText(typeRecommendation(prom));

                    } else {
                        ratingBar.setVisibility(View.INVISIBLE);
                    }
                    if(recommendCount!=0){
                        if (recommendCount == 1)
                            lblCantRecommendations.setText("1 recomendación");
                        else
                            lblCantRecommendations.setText(String.format("%d recomendaciones", recommendCount));
                    }else {
                        lblCantRecommendations.setVisibility(View.INVISIBLE);
                        lblRecommendationsDescr.setText("No tiene recomendaciones hechas");
                    }
                })
                .addOnFailureListener(err -> Log.e("ReputationFragment", "setRecommendations error: ", err));
    }

    private String typeRecommendation(float prom) {
        if (prom >= 0 && prom <= 2) {
            return "No da buenas recomendaciones";
        } else if (prom > 2 && prom <= 3) {
            return "Sus recomendaciones son regulares";
        } else if (prom > 3 && prom < 4.5) {
            return "Sus recomendaciones son muy buenas!";
        } else return "Sus recomendaciones son excelentes!";
    }
}
