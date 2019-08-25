package com.mylook.mylook.closet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    Task<Void> removeFavorite(int position) {
        return FirebaseFirestore.getInstance().collection("articles")
                .document(favoritesList.get(position).getArticleId())
                .update("favorites", FieldValue.arrayRemove(
                        FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addOnSuccessListener(task -> {
                    favoritesList.remove(position);
                    favorites.postValue(favoritesList);
                });
    }

    Task<Void> removeFavoriteFromOutfits(List<String> outfitsToChange, String idToDelete) {
        if (outfitsToChange.size() != 0) {
            WriteBatch batch = FirebaseFirestore.getInstance().batch();
            outfitsToChange.forEach(outfitDoc -> batch.update(FirebaseFirestore.getInstance()
                            .collection("outfits").document(outfitDoc),
                    "favorites", FieldValue.arrayRemove(idToDelete)));
            return batch.commit().addOnSuccessListener(task -> {
                for (int i = 0; i < outfitsList.size(); i++) {
                    if (outfitsToChange.contains(outfitsList.get(i).getOutfitId())) {
                        //TODO estas borrando el outfit cuando deberias borrar el favorito del outfit (si contiene en los articulos a idToDelete
                        outfitsList.remove(i);
                    }
                }
                outfits.postValue(outfitsList);
            });
        }
        return null;
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

    Task<Void> removeOutfit(int position) {
        return FirebaseFirestore.getInstance().collection("outfits")
                .document(outfitsList.get(position).getOutfitId()).delete()
                .addOnSuccessListener(task -> {
                    outfitsList.remove(position);
                    outfits.postValue(outfitsList);
                });
    }

    void reloadOutfits() {
        loadOutfits();
    }

    void reloadFavorites() {
        loadFavorites();
    }

}