package com.mylook.mylook.closet;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ClosetModel extends ViewModel {

    private MutableLiveData<List<Article>> favorites = new MutableLiveData<>();
    private List<Article> favoritesList = new ArrayList<>();
    private MutableLiveData<List<Outfit>> outfits = new MutableLiveData<>();
    private List<Outfit> outfitsList = new ArrayList<>();

    void load() {
        loadFavorites();
    }

    LiveData<List<Article>> getFavorites() {
        return favorites;
    }

    private void loadFavorites() {
        FirebaseFirestore.getInstance().collection("articles")
                .whereArrayContains("favorites", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        favoritesList = task.getResult().getDocuments().stream().map(document -> {
                            Article art = document.toObject(Article.class);
                            art.setArticleId(document.getId());
                            return art;
                        }).collect(Collectors.toList());
                        favorites.postValue(favoritesList);
                        loadOutfits();
                    }
                });
    }

    int removeFavorite(int position) {
        if (favorites != null) {
            if (FirebaseFirestore.getInstance().collection("articles")
                    .document(favoritesList.get(position).getArticleId())
                    .update("favorites", FieldValue.arrayRemove(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    .isSuccessful()) {
                int outfitsChanged = removeFavoriteFromOutfits(favoritesList.remove(position).getArticleId());
                favorites.postValue(favoritesList);
                outfits.postValue(outfitsList);
                return outfitsChanged;
            } else {
                return -1;
            }
        }
        return -1;
    }

    private int removeFavoriteFromOutfits(String articleId) {
        Stream<Outfit> outfitsToChange = outfitsList.stream().filter(o -> o.getFavorites().contains(articleId));
        if (outfitsToChange.count() != 0) {
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            outfitsToChange.forEach(o -> batch.update(FirebaseFirestore.getInstance()
                            .collection("outfits").document(o.getOutfitId()),
                    "favorites", FieldValue.arrayRemove(articleId)));
            if (batch.commit().isSuccessful()) {
                outfitsList.removeAll(outfitsToChange.collect(Collectors.toList()));
                return (int) outfitsToChange.count();
            } else return -1;
        } else return 0;
    }

    LiveData<List<Outfit>> getOutfits() {
        return outfits;
    }

    private void loadOutfits() {
        FirebaseFirestore.getInstance().collection("outfits")
                .whereEqualTo("userID",
                        FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        outfitsList = task.getResult().getDocuments().stream().map(document -> {
                            Outfit out = document.toObject(Outfit.class);
                            out.setOutfitId(document.getId());
                            out.setArticles(favoritesList.stream()
                                    .filter(f -> out.getFavorites().contains(f.getArticleId()))
                                    .collect(Collectors.toList()));
                            return out;
                        }).collect(Collectors.toList());
                        outfits.postValue(outfitsList);
                    }
                });
    }

    boolean removeOutfit(int position) {
        if (outfits != null) {
            if (FirebaseFirestore.getInstance().collection("outfits")
                    .document(outfitsList.get(position).getOutfitId())
                    .delete().isSuccessful()) {
                outfitsList.remove(position);
                outfits.postValue(outfitsList);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    void reloadOutfits() {
        loadOutfits();
    }
}
