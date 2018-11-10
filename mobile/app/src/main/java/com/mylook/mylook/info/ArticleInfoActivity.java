package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.storeProfile.StoreActivity;
import com.mylook.mylook.utils.ExpandableListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private ImageView backArrow, articleImage;

    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;
    private FloatingActionButton btnCloset;

    private String articleId,closetId;
    private ArrayList<String> tags;
    private String downLoadUri;
    private Article article;
    private FirebaseUser user;
    private FirebaseFirestore dB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_article_collapsing);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dB = FirebaseFirestore.getInstance();

        backArrow = findViewById(R.id.backArrow);
        expandableListView = findViewById(R.id.article_list_view_expandable);
        btnCloset=findViewById(R.id.btnCloset);
        articleImage=findViewById(R.id.article_image);
        getArticleFromIntent();
        downLoadUri=article.getPicture();
        Glide.with(mContext).load(downLoadUri).into(articleImage);
        //extensible list view
        prepareListData();
        expandableListAdapter = new ExpandableListViewAdapter(mContext, listDataGroup, listDataChild);
        expandableListView.setAdapter(expandableListAdapter);
        initExpandableListeners();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCloset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOnCloset();
            }
        });

    }
    private void getArticleFromIntent(){
        //retrieve data from intent
        Intent intent = getIntent();
        article= (Article) intent.getSerializableExtra("article");
        Log.e("ROPERO", article.getArticleId());
        articleId=article.getArticleId();
        tags = intent.getStringArrayListExtra("tags");
    }

    private void initExpandableListeners() {
        //esto hace que ande el collapsing
      expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });

        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                String storeName=listDataChild.get(listDataGroup.get(groupPosition)).get(childPosition);
                Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
                Log.d("Nombre tienda", "onClick: Paso el nombre de la tienda: " );
                intentVisitStore.putExtra("Tienda", storeName);
                mContext.startActivity(intentVisitStore);

                return false;
            }
        });

        // ExpandableListView Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(),
                        //listDataGroup.get(groupPosition),
                        //Toast.LENGTH_SHORT).show();
            }
        });

        // ExpandableListView Group collapsed listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
               // Toast.makeText(getApplicationContext(),
                 //       listDataGroup.get(groupPosition),
                //     Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void prepareListData() {

        listDataGroup = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataGroup.add("Tienda");
        listDataGroup.add("Talles");
        listDataGroup.add("Colores");
        listDataGroup.add("Materiales");
        listDataGroup.add("Stock");

        List<String> infoTienda = new ArrayList<String>();
        Log.d("info tienda", "info intent: " );
        infoTienda.add(article.getStoreName());
        List<String> infoTalles = article.getSizes();
        List<String> infoColores = article.getColors();
        List<String> infoMateriales = new ArrayList<String>();
        infoMateriales.add(article.getMaterial());
        List<String> infoStock = new ArrayList<String>();
        infoStock.add(String.valueOf(article.getInitial_stock()));

        listDataChild.put(listDataGroup.get(0),infoTienda);
        listDataChild.put(listDataGroup.get(1),infoTalles);
        listDataChild.put(listDataGroup.get(2),infoColores);
        listDataChild.put(listDataGroup.get(3),infoMateriales);
        listDataChild.put(listDataGroup.get(4),infoStock);

    }

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }
    private void saveOnCloset() {

        final Map<String, Object> favorites = new HashMap<>();
        favorites.put("articleId", articleId);
        favorites.put("downloadUri", downLoadUri);
        favorites.put("collecion", null);

        dB.collection("closets")
                .whereEqualTo("userID",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().getDocuments().size()>0){
                            closetId=task.getResult().getDocuments().get(0).getId();
                            dB.collection("closets").document(closetId).collection("favorites")
                                    .whereEqualTo("articleId",articleId).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.getResult().getDocuments().size()==0){
                                                Log.e("CLOSET", closetId);
                                                dB.collection("closets").document(closetId).collection("favorites").add(favorites);
                                                sendNewInteraction();
                                                displayMessage("Se añadió a tu ropero");
                                            }else
                                            {
                                                displayMessage("Ya es favorito");
                                            }
                                        }
                                    });
                        }else
                        {
                            displayMessage("No tienes un ropero asociado");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        displayMessage("Error al guardar en ropero");
                    }
                });


    }
    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNewInteraction(){
        Interaction userInteraction = new Interaction();
        userInteraction.setSavedToCloset(true);
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(false);
        userInteraction.setArticleId(this.articleId);
        userInteraction.setStoreName(this.article.getStoreName());
        userInteraction.setTags(tags);
        userInteraction.setUserId(user.getUid());
        dB.collection("interactions").add(userInteraction);
    }
}
