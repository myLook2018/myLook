package com.mylook.mylook.explore;

import android.location.Location;
import android.util.Log;

import com.mylook.mylook.entities.Article;

class LocationValidator {

    private static double getGreatCircleDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double x1 = Math.toRadians(latitude1);
        double y1 = Math.toRadians(longitude1);
        double x2 = Math.toRadians(latitude2);
        double y2 = Math.toRadians(longitude2);
        double res = 6371000 * Math.acos(Math.sin(x1) * Math.sin(x2) + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2));
        Log.e("Distance", "getGreatCircleDistance: " + res);
        return res;
    }

    static boolean checkIfNearby(Article article, Location location, double distance) {
        Log.e("Article", "checkIfNearby: " + article.getStoreLatitude() + " - " + article.getStoreLongitude() + " - " + distance);
        return (getGreatCircleDistance(article.getStoreLatitude(), article.getStoreLongitude(), location.getLatitude(), location.getLongitude()) <= distance);
    }

}
