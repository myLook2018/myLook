package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v7.widget.Toolbar;

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
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

public class ClosetActivity extends AppCompatActivity {

    private FirebaseFirestore dB;
    private FirebaseUser user;
    private Closet closet;
    private ArrayList<Favorite> favorites;
    private Toolbar tb;

    private GridView gridview;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);
        dB=FirebaseFirestore.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        initElements();
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 3;
        gridview.setColumnWidth(imageWidth);
        gridview.setHorizontalSpacing(8);
        gridview.setNumColumns(3);
        getCloset();



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                dB.collection("articles").document((String) parent.getAdapter().getItem(position)).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    String articleId=task.getResult().getId();
                                    Article art= task.getResult().toObject(Article.class);
                                    art.setArticleId(articleId);
                                    Intent intent = new Intent(ClosetActivity.this, ArticleInfoActivity.class);
                                    intent.putExtra("article",art);
                                    getApplicationContext().startActivity(intent);
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });
            }
        });
    }
    private void initElements(){
        tb=findViewById(R.id.toolbar);
        tb.setTitle("Mi Ropero");
        setSupportActionBar(tb);
        gridview =  findViewById(R.id.gridview);
        favorites=new ArrayList<Favorite>();
    }

    private void getCloset(){
        Log.e("GET_CLOSET","--");
        dB.collection("closets")
                .whereEqualTo("userID",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                closet=document.toObject(Closet.class);
                                String id= document.getId();
                                Log.e("FAVORITESIDDDDDDDDDDDD",id);
                                dB.collection("closets").document(id).collection("favorites").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    ArrayList<String>arrayList=new ArrayList<>();
                                                    for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                                    {
                                                        Favorite fav=documentSnapshot.toObject(Favorite.class);
                                                        favorites.add(fav);
                                                        arrayList.add(fav.getDownloadUri());
                                                        Log.e("FAVORITES","---");
                                                    }

                                                    GridImageAdapter gridAdapter = new GridImageAdapter(ClosetActivity.this, R.layout.activity_closet, arrayList);
                                                    gridview.setAdapter(new com.mylook.mylook.utils.ImageAdapter(ClosetActivity.this,favorites));
                                                }else
                                                    Log.e("FAVORITES","Nuuuuuuuuuuuuuuuuuuuuuu");
                                            }
                                        });
                            }
                        }else{
                            Log.e("FAVORITES","NOOOOOOOOOOOOO");
                        }
                    }
                });
    }
}
