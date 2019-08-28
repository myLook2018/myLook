package com.mylook.mylook.premiumUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;

import java.util.Calendar;
import java.util.Date;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

@SuppressLint("ValidFragment")
public class ReputationPremiumFragment extends Fragment {

    private TextView lblActiveStore;
    private TextView lblDate;
    private TextView lblCant;
    private MaterialRatingBar ratingBar;
    private TextView lblCantRecommendations;
    private TextView lblRecommendationsDescr;
    private  FirebaseFirestore dB;
    private String clientId;
    private float ratingSum=0;
    private int recommendCount=0;
    private String registerDate;
    private TextView lblMylookUser;

    public ReputationPremiumFragment() {
    }

    @SuppressLint("ValidFragment")
    public ReputationPremiumFragment(String clientId) {
        dB=FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_reputation, container, false);
        initElements(rootView);
        //countRecommendations();
        countSubscribers();
        lblDate.setText((CharSequence)  registerDate);
        return rootView;
    }
    public void initElements(View rootView){
        lblMylookUser=rootView.findViewById(R.id.lblMylookUser);
        lblMylookUser.setText("Es Usuario Destacado desde");
        lblActiveStore=rootView.findViewById(R.id.lblActiveStore);
        lblActiveStore.setText("Es un Usuario Activo!");
        lblDate=rootView.findViewById(R.id.lblDate);
        lblCant=rootView.findViewById(R.id.lblCant);
        //ratingBar=rootView.findViewById(R.id.ratingBar);
        //lblCantRecommendations=rootView.findViewById(R.id.lblCantRecommendations);
        //lblRecommendationsDescr=rootView.findViewById(R.id.lblRecomendationsDescr);
    }

    public void setRegisterDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        int mes = cal.get(Calendar.MONTH) + 1;
        registerDate= cal.get(Calendar.DAY_OF_MONTH) + "/" + mes + "/" + cal.get(Calendar.YEAR);
    }

    public void countRecommendations(){
        dB.collection("answeredRecommendations").whereEqualTo("clientId", clientId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot:task.getResult()){
                    ratingSum+=Float.parseFloat((String)documentSnapshot.get("feedBack"));
                    recommendCount++;
                }
                if(ratingSum!=0 && recommendCount!=0){
                    float prom=ratingSum/recommendCount;
                    ratingBar.setRating(prom);
                    ratingBar.setEnabled(false);
                    if(recommendCount==1)
                        lblCantRecommendations.setText("Recomendó 1 vez");
                    else
                        lblCantRecommendations.setText(String.format("Recomendó %d veces", recommendCount));
                    lblRecommendationsDescr.setText(typeRecommendation(prom));
                    recommendCount=0;
                    ratingSum=0;
                }else{
                    ratingBar.setVisibility(View.INVISIBLE);
                    lblCantRecommendations.setVisibility(View.INVISIBLE);
                    lblRecommendationsDescr.setText("Aún no realizó ninguna recomendación!");
                }
            }
        });
    }

    public void countSubscribers() {
        dB.collection("premiumUsersSubscriptions").whereEqualTo("clientId", clientId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int cant=task.getResult().getDocuments().size();
                        if(cant==0)
                            lblCant.setText("Todavia no tiene suscriptores");
                        else if(cant==1)
                            lblCant.setText("1 suscriptor");
                            else
                                lblCant.setText(String.format("%d suscriptores!", cant));
                    }
                });
    }

    public String typeRecommendation(float prom){
        String description="";
        if(prom>=0 && prom<=2){
            description="No da buenas recomendaciones";
        }else if(prom>2 && prom<=3){
            description="Sus recomendaciones son regulares";
                }else if(prom>3 && prom<4.5){
                description="Sus recomendaciones son muy buenas!";
                        }else description="Sus recomendaciones son excelentes!";
        return description;
    }
}
