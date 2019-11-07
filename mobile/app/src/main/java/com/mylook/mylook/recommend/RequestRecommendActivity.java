package com.mylook.mylook.recommend;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.session.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class RequestRecommendActivity extends AppCompatActivity {

    private static final String TAG = "RequestRecommendation";
    private ImageView imgRequestPhoto;
    private TextView txtDescription, sizeText, categoryText, sizeLabel, categoryLabel;
    private TextView txtTitle;
    private RecyclerView recyclerView;
    private TextView txtLimitDate;
    private ArrayList<HashMap<String, String>> answers;
    private String requestId;
    private boolean isClosed = false;
    private Menu optionsMenu;
    private ShareActionProvider mShareActionProvider;
    private boolean fromDeepLink = false;


    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation);
        Log.d(TAG, "onCreate: started.");
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Tu Solicitud");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        imgRequestPhoto = findViewById(R.id.imgRecommend);
        txtDescription = findViewById(R.id.txtRecommendDescpription);
        txtTitle = findViewById(R.id.txtRecommendTitle);
        txtLimitDate = findViewById(R.id.txtDate);
        sizeText = findViewById(R.id.sizeText);
        sizeLabel = findViewById(R.id.sizeLabel);
        categoryText = findViewById(R.id.categoryText);
        categoryLabel = findViewById(R.id.categoryLabel);
        getIncomingIntent();
        invalidateOptionsMenu();

    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        onBackPressed();
        return true;
    }

    private void initRecyclerView(ArrayList<HashMap<String, String>> answerList) {
        recyclerView = findViewById(R.id.rVAnswer);
        AnswersRecyclerViewAdapter adapter = new AnswersRecyclerViewAdapter(RequestRecommendActivity.this, answerList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void getIncomingIntent() {
        Intent intent = getIntent();
        Log.e("Extras", intent.getExtras().toString());
        for (String key:intent.getExtras().keySet()) {
            Log.e(TAG, key+": "+intent.getExtras().get(key).toString());
        }
        if (intent.hasExtra("requestRecommendation")) {
            fromDeepLink = false;
            RequestRecommendation requestRecommendation = (RequestRecommendation) intent.getSerializableExtra("requestRecommendation");
            requestId = requestRecommendation.getDocumentId();
        } else if(intent.hasExtra("requestId")){
            fromDeepLink = true;
            requestId = intent.getStringExtra("requestId");
        }else {
            fromDeepLink = true;
            // Cuando viene de un deeplink
            Uri data  = intent.getData();
            try {
                requestId = data.getQueryParameter("requestId");
            } catch(Exception e){
                if(intent.hasExtra("requestId"))
                    requestId = intent.getStringExtra("requestId");
            }
            if(getIntent().hasExtra("fromDeepLink")){
                fromDeepLink = true;
            }
        }

        Log.e(TAG, "requestId"+requestId);
        FirebaseFirestore.getInstance().collection("requestRecommendations").document(requestId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            txtDescription.setText(doc.get("description").toString());
                            txtTitle.setText(doc.get("title").toString());

                            if(doc.contains("size") && !doc.get("size").toString().isEmpty()) {
                                sizeText.setText(doc.get(("size")).toString().toUpperCase());
                                sizeLabel.setText("Talle: ");
                            } else{
                                sizeText.setVisibility(View.GONE);
                                sizeLabel.setVisibility(View.GONE);
                            }
                            if(doc.contains("category") && !doc.get("category").toString().isEmpty()) {
                                categoryText.setText(doc.get("category").toString());
                                categoryLabel.setText("Categoría: ");
                            } else {
                                categoryText.setVisibility(View.GONE);
                                categoryLabel.setVisibility(View.GONE);
                            }
                            isClosed = (boolean) doc.get("isClosed");
                            if(isClosed){
                                txtLimitDate.setText("Cerrada");
                            }else {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis((long) doc.get("limitDate"));
                                int mes = cal.get(Calendar.MONTH) + 1;
                                final String dateFormat = cal.get(Calendar.DAY_OF_MONTH) + "/" + mes + "/" + cal.get(Calendar.YEAR);
                                txtLimitDate.setText("Hasta el " + dateFormat);
                            }
                            setImage(doc.get("requestPhoto").toString());
                            answers = (ArrayList<HashMap<String, String>>) doc.get("answers");
                            initRecyclerView(answers);
                        } else {
                            Log.e(TAG, "busqueda not succesful");
                        }
                        invalidateOptionsMenu();
                    }
                }
        );
    }

    private void setImage(String imageUrl) {
        Log.d(TAG, "setImage: setting te image and name to widgets.");
        try{
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(imgRequestPhoto);
        }catch (Exception e){
            Log.e("RequestRecommActivity", "Exception: "+e.getMessage());
        }


    }


    private void updateAnswers(){
        FirebaseFirestore.getInstance().collection("requestRecommendations").document(requestId).update("answers", answers).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "Respuestas actualizadas");
                    }
                }
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseFirestore.getInstance().collection("requestRecommendations").document(requestId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<HashMap> remoteAnswers = (ArrayList<HashMap>) doc.get("answers");
                if (remoteAnswers.size() != answers.size()){
                    for (HashMap remoteAnswer: remoteAnswers) {
                        boolean newRecommendation = true;
                        for(HashMap localAnswer: answers){
                            if(localAnswer.get("storeName").toString().equals(remoteAnswer.get("storeName"))){
                                newRecommendation = false;
                            }
                        }
                        if (newRecommendation){
                            answers.add(remoteAnswer);
                        }
                    }
                }
                updateAnswers();
            }
        });
        FirebaseFirestore.getInstance().collection("answeredRecommendations").whereEqualTo("requestUID", requestId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(answers!=null){
                            for (HashMap<String,String> answer: answers ){
                                for (DocumentSnapshot doc:task.getResult().getDocuments()){
                                    if(doc.get("storeName").equals(answer.get("storeName")))
                                    {
                                        FirebaseFirestore.getInstance().collection("answeredRecommendations").document(doc.getId()).update("feedBack", answer.get("feedBack"));
                                    }

                                }
                            }
                        }
                    }
                });
        if(!fromDeepLink)
            Session.getInstance().updateActivitiesStatus(Session.RECOMEND_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "ON back pressed");
        Log.e(TAG, "From deep link"+fromDeepLink);
        if(fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.request_recommendation_menu, menu);
        if(isClosed) {
            menu.findItem(R.id.close_req).setVisible(false); // si ya esta cerrada no muestro la opcion
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.share_req){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "¿Me podés ayudar con esta recomendación? https://www.mylook.com/recommendation?requestId="+requestId);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        }
//        if(id == R.id.delete_req){
//            // do something
//        }
        if(id == R.id.close_req){
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
            final android.app.AlertDialog alert = dialog.setTitle("Cerrar solicitud")
                    .setMessage("¿Estás seguro que querés cerrar esta solicitud?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            FirebaseFirestore.getInstance().collection("requestRecommendations").document(requestId).update("isClosed", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Tu solicitud ha sido cerrado", Toast.LENGTH_LONG).show();
                                }

                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        }


                    }).create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purple));
                    alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
                }
            });
            alert.show();

        }
        return super.onOptionsItemSelected(item);
    }


}