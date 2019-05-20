package com.mylook.mylook.closet;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;

import java.util.List;
import java.util.stream.Collectors;

public class FavoritesViewModel extends ViewModel {

    private MutableLiveData<List<Article>> favorites;
    private MutableLiveData<List<Outfit>> outfits;

    public FavoritesViewModel() {
    }

    LiveData<List<Article>> getFavorites() {
        if (favorites == null) {
            favorites = new MutableLiveData<>();
            loadFavorites();
        }
        return favorites;
    }

    private void loadFavorites() {
        FirebaseFirestore.getInstance().collection("articles")
                .whereArrayContains("favorites",
                        FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        favorites.setValue(task.getResult().getDocuments().stream().map(document -> {
                            Article art = document.toObject(Article.class);
                            art.setArticleId(document.getId());
                            return art;
                        }).collect(Collectors.toList()));
                    }
                });
    }

    private Article removeFavorite(int position) {
        if (favorites == null) return null;
        return favorites.getValue().remove(position);
    }

    public LiveData<List<Outfit>> getOutfits() {
        if (favorites == null) {
            favorites = new MutableLiveData<>();
            loadFavorites();
        }
        if (outfits == null) {
            outfits = new MutableLiveData<>();
            loadOutfits();
        }
        return outfits;
    }

    private void loadOutfits() {
        FirebaseFirestore.getInstance().collection("outfits")
                .whereEqualTo("userID",
                        FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        outfits.setValue(task.getResult().getDocuments().stream()
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

    private Outfit removeOutfit(int position) {
        if (outfits == null) return null;
        return outfits.getValue().remove(position);
    }

}
