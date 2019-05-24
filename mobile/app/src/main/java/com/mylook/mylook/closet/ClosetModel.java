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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ClosetModel extends ViewModel {

    private MutableLiveData<List<Article>> favorites;
    private MutableLiveData<List<Outfit>> outfits;

    LiveData<List<Article>> getFavorites() {
        if (favorites == null) {
            loadFavorites();
            loadOutfits();
        }
        return favorites;
    }

    private void loadFavorites() {
        favorites = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereArrayContains("favorites", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        favorites.postValue(task.getResult().getDocuments().stream().map(document -> {
                            Article art = document.toObject(Article.class);
                            art.setArticleId(document.getId());
                            return art;
                        }).collect(Collectors.toList()));
                    }
                });
    }

    int removeFavorite(int position) {
        if (favorites != null) {
            if (FirebaseFirestore.getInstance().collection("articles")
                    .document(favorites.getValue().get(position).getArticleId())
                    .update("favorites", FieldValue.arrayRemove(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    .isSuccessful()) {
                int outfitsChanged = removeFavoriteFromOutfits(favorites.getValue().remove(position).getArticleId());
                favorites.postValue(favorites.getValue());
                outfits.postValue(outfits.getValue());
                return outfitsChanged;
            } else {
                return -1;
            }
        }
        return -1;
    }

    private int removeFavoriteFromOutfits(String articleId) {
        Stream<Outfit> outfitsToChange = outfits.getValue().stream().filter(o -> o.getItems().contains(articleId));
        if (outfitsToChange.count() != 0) {
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            outfitsToChange.forEach(o -> batch.update(FirebaseFirestore.getInstance()
                            .collection("outfits").document(o.getOutfitId()),
                    "favorites", FieldValue.arrayRemove(articleId)));
            if (batch.commit().isSuccessful()) {
                return (int) outfitsToChange.count();
            } else {
                return -1;
            }
        } else return 0;
    }

    LiveData<List<Outfit>> getOutfits() {
        return outfits;
    }

    private void loadOutfits() {
        outfits = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection("outfits")
                .whereEqualTo("userID",
                        FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        outfits.postValue(task.getResult().getDocuments().stream()
                                .map(document -> {
                                    Outfit out = document.toObject(Outfit.class);
                                    out.setOutfitId(document.getId());
                                    out.setArticles(favorites.getValue().stream()
                                            .filter(f -> out.getItems().contains(f.getArticleId()))
                                            .collect(Collectors.toList()));
                                    return out;
                                }).collect(Collectors.toList()));
                    }
                });
    }

    boolean removeOutfit(int position) {
        if (outfits != null) {
            if (FirebaseFirestore.getInstance().collection("outfits")
                    .document(outfits.getValue().get(position).getOutfitId())
                    .delete().isSuccessful()) {
                outfits.getValue().remove(position);
                outfits.postValue(outfits.getValue());
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
