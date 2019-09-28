package com.mylook.mylook.recommend;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private TextView txtDescription;
    private TextView txtTitle;
    private RecyclerView recyclerView;
    private TextView txtLimitDate;
    private ArrayList<HashMap<String, String>> answers;
    private FirebaseFirestore dB;
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
        tb.setTitle("Tu solicitud");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        imgRequestPhoto = findViewById(R.id.imgRecommend);
        txtDescription = findViewById(R.id.txtRecommendDescpription);
        txtTitle = findViewById(R.id.txtRecommendTitle);
        txtLimitDate = findViewById(R.id.txtDate);
        this.dB = FirebaseFirestore.getInstance();
        getIncomingIntent();
        invalidateOptionsMenu();
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
        }
        //ACA LLEGA DISTINTO
        Log.e(TAG, "requestId"+requestId);
        dB.collection("requestRecommendations").document(requestId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            txtDescription.setText(doc.get("description").toString());
                            txtTitle.setText(doc.get("title").toString());
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis((long) doc.get("limitDate"));
                            int mes = cal.get(Calendar.MONTH) + 1;
                            final String dateFormat = cal.get(Calendar.DAY_OF_MONTH) + "/" + mes + "/" + cal.get(Calendar.YEAR);
                            txtLimitDate.setText("Hasta el " + dateFormat);
                            setImage(doc.get("requestPhoto").toString());
                            answers = (ArrayList<HashMap<String, String>>) doc.get("answers");
                            isClosed = (boolean) doc.get("isClosed");
                            initRecyclerView(answers);
                        } else {
                            Log.e(TAG, "busqueda not succesful");
                        }
                    }
                }
        );
    }

    private void setImage(String imageUrl) {
        Log.d(TAG, "setImage: setting te image and name to widgets.");

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(imgRequestPhoto);
    }


    private void updateAnswers(){
        dB.collection("requestRecommendations").document(requestId).update("answers", answers).addOnCompleteListener(
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
        dB.collection("requestRecommendations").document(requestId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        dB.collection("answeredRecommendations").whereEqualTo("requestUID", requestId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (HashMap<String,String> answer: answers ){
                            for (DocumentSnapshot doc:task.getResult().getDocuments()){
                                if(doc.get("storeName").equals(answer.get("storeName")))
                                {
                                    dB.collection("answeredRecommendations").document(doc.getId()).update("feedBack", answer.get("feedBack"));
                                }

                            }
                        }
                    }
                });
        Session.getInstance().updateActivitiesStatus(Session.RECOMEND_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isClosed) {
            getMenuInflater().inflate(R.menu.request_recommendation_menu, menu);
            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.share_req);

            // Fetch and store ShareActionProvider
            //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        } else {
//            menu.getItem(0).setVisible(false);
//            menu.getItem(1).setVisible(false);
//            menu.getItem(2).setVisible(false);
//            menu.getItem(3).setVisible(false);
            return false;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.share_req){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Me podes ayudar con esta recomendación? https://www.mylook.com/recommendation?requestId="+requestId);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share via"));
        }
//        if(id == R.id.delete_req){
//            // do something
//        }
        if(id == R.id.close_req){
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme);
            final android.app.AlertDialog alert = dialog.setTitle("Cerrar pedido")
                    .setMessage("¿Estás seguro que querés cerrar este pedido?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            dB.collection("requestRecommendations").document(requestId).update("isClosed", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Tu pedido ha sido cerrado", Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}