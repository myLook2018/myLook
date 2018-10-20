package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private ImageView backArrow, articleImage;
    private Button visitStore;
    private TextView articleStore, articleCost, articleStock, articleColors, articleMaterial, articlesSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_article);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        visitStore = (Button) findViewById(R.id.btn_visit_store);
        articleColors = (TextView) findViewById(R.id.article_colors);
        articleCost = (TextView) findViewById(R.id.article_cost);
        articleMaterial = (TextView) findViewById(R.id.article_material);
        articlesSize = (TextView) findViewById(R.id.article_size);
        articleStock = (TextView) findViewById(R.id.article_stock);
        articleStore = (TextView) findViewById(R.id.article_store);
        articleImage = (ImageView) findViewById(R.id.article_image);

        //retrieve data from intent
        final Intent intent = getIntent();
        articleStore.setText("Encontralo en: " + intent.getStringExtra("Tienda"));
        articleCost.setText("Precio($): " + intent.getStringExtra("Costo"));
        articleStock.setText("Stock: " + intent.getStringExtra("Stock"));
        articleColors.setText("Colores: " + intent.getStringExtra("Colores"));
        articleMaterial.setText("Material: " + intent.getStringExtra("Material"));
        articlesSize.setText("Talles: " + intent.getStringExtra("Talle"));

        Glide.with(mContext).load(intent.getStringExtra("Foto")).into(articleImage);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        visitStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
                Log.d("Nombre tienda", "onClick: Paso el nombre de la tienda: " + intent.getStringExtra("Tienda"));
                intentVisitStore.putExtra("Tienda", intent.getStringExtra("Tienda"));
                mContext.startActivity(intentVisitStore);
            }
        });


    }
}
