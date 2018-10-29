package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.utils.ExpandableListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private ImageView backArrow, articleImage;
    //private TextView articleStore, articleCost, articleStock, articleColors, articleMaterial, articlesSize,
    private TextView articleTitle, articleCost;

    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_article_collapsing);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        //articleColors = (TextView) findViewById(R.id.lblColors);
        //articleCost = (TextView) findViewById(R.id.article_cost);
        //articleMaterial = (TextView) findViewById(R.id.lblMaterial);
        //articlesSize = (TextView) findViewById(R.id.lblSizes);
        //articleStock = (TextView) findViewById(R.id.lblstock);
        //articleStore = (TextView) findViewById(R.id.lblstore);
        articleImage = (ImageView) findViewById(R.id.article_image);
        articleTitle=(TextView)findViewById(R.id.lblTitle);
        expandableListView = (ExpandableListView) findViewById(R.id.article_list_view_expandable);

        //retrieve data from intent
        intent = getIntent();
        //articleStore.setText(intent.getStringExtra("Tienda"));
        //articleCost.setText("$" + intent.getStringExtra("Costo"));
        //articleStock.setText("Stock: " + intent.getStringExtra("Stock"));
        //articleColors.setText("Colores: " + intent.getStringExtra("Colores"));
        //articleMaterial.setText("Material: " + intent.getStringExtra("Material"));
        //articlesSize.setText("Talles: " + intent.getStringExtra("Talle"));
        articleTitle.setText(intent.getStringExtra("Title"));

        Glide.with(mContext).load(intent.getStringExtra("Foto")).into(articleImage);

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

        /*articleStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
                Log.d("Nombre tienda", "onClick: Paso el nombre de la tienda: " + intent.getStringExtra("Tienda"));
                intentVisitStore.putExtra("Tienda", intent.getStringExtra("Tienda"));
                mContext.startActivity(intentVisitStore);
            }
        });*/
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
                Toast.makeText(
                        getApplicationContext(),
                        listDataGroup.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataGroup.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        // ExpandableListView Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataGroup.get(groupPosition),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ExpandableListView Group collapsed listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataGroup.get(groupPosition),
                        Toast.LENGTH_SHORT).show();

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
        Log.d("info tienda", "info intent: " + intent.getStringExtra("Tienda"));
        infoTienda.add(intent.getStringExtra("Tienda"));


        List<String> infoTalles = new ArrayList<String>();
        infoTalles.add(intent.getStringExtra("Talle"));


        List<String> infoColores = new ArrayList<String>();
        infoColores.add(intent.getStringExtra("Colores"));


        List<String> infoMateriales = new ArrayList<String>();
        infoMateriales.add(intent.getStringExtra("Material"));


        List<String> infoStock = new ArrayList<String>();
        infoStock.add(intent.getStringExtra("Stock"));

        listDataChild.put(listDataGroup.get(0),infoTienda);
        listDataChild.put(listDataGroup.get(1),infoTalles);
        listDataChild.put(listDataGroup.get(2),infoColores);
        listDataChild.put(listDataGroup.get(3),infoMateriales);
        listDataChild.put(listDataGroup.get(4),infoStock);

    }

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
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
}
