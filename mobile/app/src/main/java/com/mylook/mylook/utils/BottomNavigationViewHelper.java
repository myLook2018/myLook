package com.mylook.mylook.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.mylook.mylook.closet.ClosetActivity;
import com.mylook.mylook.explore.ExploreActivity;
import com.mylook.mylook.home.MainActivity;
import com.mylook.mylook.profile.ProfileActivity;
import com.mylook.mylook.R;
import com.mylook.mylook.recommend.RecommendationsActivity;

public class BottomNavigationViewHelper {

    public static void enableNavigation(final Context context, @NonNull BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, MainActivity.class);
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_explore:
                        Intent intent2 = new Intent(context, ExploreActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_recommend:
                        Intent intent3 = new Intent(context, RecommendationsActivity.class);
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_closet:
                        Intent intent4 = new Intent(context, ClosetActivity.class);
                        context.startActivity(intent4);
                        break;
                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent5);
                        break;
                }

                return false;
            }
        });
    }

}
