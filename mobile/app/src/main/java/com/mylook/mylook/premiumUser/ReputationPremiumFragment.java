package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;

import java.util.Calendar;
import java.util.Date;

@SuppressLint("ValidFragment")
public class ReputationPremiumFragment extends Fragment {

    private TextView lblDate;
    private TextView lblCant;
    private float ratingSum=0;
    private int recommendCount=0;
    private String registerDate;
    private String clientIdPremium;

    public ReputationPremiumFragment() {
    }

    @SuppressLint("ValidFragment")
    public ReputationPremiumFragment(String userId) {
        this.clientIdPremium =userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_reputation, container, false);
        initElements(rootView);
        //countRecommendations();
        return rootView;
    }
    public void initElements(View rootView){
        TextView lblMylookUser = rootView.findViewById(R.id.lblMylookUser);
        lblMylookUser.setText("Es Usuario Destacado desde");
        TextView lblActiveStore = rootView.findViewById(R.id.lblActiveStore);
        lblActiveStore.setText("Es un Usuario Activo!");
        CardView carRecomm= rootView.findViewById(R.id.cardRecommendations);
        carRecomm.setVisibility(View.GONE);
        lblDate=rootView.findViewById(R.id.lblDate);
        lblDate.setVisibility(View.VISIBLE);
        lblCant=rootView.findViewById(R.id.lblCant);
        countSubscribers();

        Bundle args = getArguments();

        if (args != null) {
            Date date=(Date) args.getSerializable("registerDate");
            if (date!=null)setRegisterDate(date);
            else lblDate.setText("Gise pasame este dato"); //TODO Ver si se puede tomar del FirebaseUID
        }


        }

    public void setRegisterDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        lblDate.setText(String.format("%s/%s/%s",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)));
    }

    public void countSubscribers() {
        FirebaseFirestore.getInstance().collection("premiumUsersSubscriptions")
                .whereEqualTo("storeName", clientIdPremium).get()
                .addOnCompleteListener(task -> {
                    int cant=task.getResult().getDocuments().size();
                    if(cant==0)
                        lblCant.setText("Todavía no tiene suscriptores");
                    else if(cant==1)
                        lblCant.setText("1 suscriptor");
                        else
                            lblCant.setText(String.format("%d suscriptores!", cant));
                });
    }

}
