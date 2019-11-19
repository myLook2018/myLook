package com.mylook.mylook.explore;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.entities.PremiumPublication;
import com.mylook.mylook.room.AppDatabase;
import com.mylook.mylook.room.LocalInteraction;
import com.mylook.mylook.room.LocalInteractionDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

class ExploreService {

    /**
     * LCM of 9, 7, 5, 3:
     * BOUND_1_2_3 = 9
     * BOUND_2_3 = 7
     * BOUND_1_2 = 5
     * BOUND_1_3 = 3
     * Logic:
     * P(3) = 2 x P(1)
     * P(2) = 1.5 x P(1)
     * P(3) = 1,333... x P(2)
     */
    private static final int BOUND = 315;

    private static final int LOWER_LIMIT_OF_3_IN_1_2_3 = 175; // 5/9
    private static final int LOWER_LIMIT_OF_2_IN_1_2_3 = 70; // 2/9
    private static final int LOWER_LIMIT_OF_3_IN_2_3 = 135; // 3/7
    private static final int LOWER_LIMIT_OF_2_IN_1_2 = 126; // 2/5
    private static final int LOWER_LIMIT_OF_3_IN_1_3 = 105; // 1/3

    private String userUid;
    private LocalInteractionDAO localDAO;
    private ArrayList<Interaction> interactions;
    private List<LocalInteraction> allLocalInteractions;
    private List<LocalInteraction> currentLocalInteractions;

    ExploreService(Context context) {
        userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        localDAO = AppDatabase.getDatabase(context).getLocalInteractionDAO();
        allLocalInteractions = localDAO.getAllByUser(userUid);
        currentLocalInteractions = new ArrayList<>();
        interactions = new ArrayList<>();
    }

    List<Object> createExploreArticleList(QuerySnapshot query, Location location, double distance) {
        List<Article> promo1 = new ArrayList<>();
        List<Article> promo2 = new ArrayList<>();
        List<Article> promo3 = new ArrayList<>();
        List<Object> articles = new ArrayList<>();

        for (QueryDocumentSnapshot document : query) {
            if (isNew(document.getId())) {
                Article article = document.toObject(Article.class);
                article.setArticleId(document.getId());
                if (location != null) {
                    article.setNearby(LocationValidator.checkIfNearby(article, location, distance));
                    if (!article.isNearby()) {
                        continue;
                    }
                }
                switch (article.getPromotionLevel()) {
                    case 1:
                        promo1.add(article);
                        break;
                    case 2:
                        promo2.add(article);
                        break;
                    case 3:
                        promo3.add(article);
                        break;
                }
            }
        }
        if (!promo3.isEmpty()) Collections.shuffle(promo3);
        if (!promo2.isEmpty()) Collections.shuffle(promo2);
        if (!promo1.isEmpty()) Collections.shuffle(promo1);

        Random r = new Random();
        int v;
        while (true) {
            if (!promo3.isEmpty()) {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(BOUND);
                        if (v > LOWER_LIMIT_OF_3_IN_1_2_3) articles.add(promo3.remove(0));
                        else if (v > LOWER_LIMIT_OF_2_IN_1_2_3) articles.add(promo2.remove(0));
                        else articles.add(promo1.remove(0));
                    } else {
                        v = r.nextInt(BOUND);
                        if (v > LOWER_LIMIT_OF_3_IN_2_3) articles.add(promo3.remove(0));
                        else articles.add(promo2.remove(0));
                    }
                } else {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(BOUND);
                        if (v > LOWER_LIMIT_OF_3_IN_1_3) articles.add(promo3.remove(0));
                        else articles.add(promo1.remove(0));
                    } else articles.add(promo3.remove(0));
                }
            } else {
                if (!promo2.isEmpty()) {
                    if (!promo1.isEmpty()) {
                        v = r.nextInt(BOUND);
                        if (v > LOWER_LIMIT_OF_2_IN_1_2) articles.add(promo2.remove(0));
                        else articles.add(promo1.remove(0));
                    } else articles.add(promo2.remove(0));
                } else {
                    if (!promo1.isEmpty()) articles.add(promo1.remove(0));
                    else break;
                }
            }
        }

        return articles;
    }

    List<Object> addPremiumPublicationsToList(List<Object> publications, QuerySnapshot query) {
        Random r = new Random();
        int index;
        for (QueryDocumentSnapshot document : query) {
            PremiumPublication premiumPublication = document.toObject(PremiumPublication.class);
            premiumPublication.setPremiumPublicationId(document.getId());
            if(!publications.isEmpty()) //r.nextInt() da exception si recibe <=0
            {
                index = r.nextInt(publications.size());
                publications.add(index, premiumPublication);

            }
        }
        return publications;
    }

    private boolean isNew(String id) {
        // TODO incluir en produccion
         return allLocalInteractions.stream().noneMatch(li -> li.getUid().equals(id));
        //return true;
    }

    void likeArticle(Article article, boolean liked) {
        Interaction userInteraction = new Interaction();
        userInteraction.setSavedToCloset(false);
        userInteraction.setClickOnArticle(false);
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setLiked(liked);
        userInteraction.setArticleId(article.getArticleId());
        userInteraction.setStoreName(article.getStoreName());
        userInteraction.setTags(article.getTags());
        userInteraction.setUserId(userUid);
        userInteraction.setTitle(article.getTitle());
        interactions.add(userInteraction);

        LocalInteraction local = new LocalInteraction();
        local.setUid(article.getArticleId());
        local.setUserId(userUid);
        local.setDate(Calendar.getInstance().getTime());
        currentLocalInteractions.add(local);
    }

    void likePremiumPublication(PremiumPublication premiumPublication, boolean liked) {
        // TODO check this
        /*
        Interaction userInteraction = new Interaction();
        userInteraction.setSavedToCloset(false);
        userInteraction.setClickOnArticle(false);
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setLiked(liked);
        userInteraction.setArticleId(article.getArticleId());
        userInteraction.setStoreName(article.getStoreName());
        userInteraction.setTags(article.getTags());
        userInteraction.setUserId(userUid);
        interactions.add(userInteraction);

        LocalInteraction local = new LocalInteraction();
        local.setUid(article.getArticleId());
        local.setUserId(userUid);
        local.setDate(Calendar.getInstance().getTime());
        currentLocalInteractions.add(local);
        */
    }

    void visitArticle(Article article) {
        Interaction userInteraction = new Interaction();
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(true);
        userInteraction.setArticleId(article.getArticleId());
        userInteraction.setStoreName(article.getStoreName());
        userInteraction.setTags(article.getTags());
        userInteraction.setUserId(userUid);
        userInteraction.setTitle(article.getTitle());
        interactions.add(userInteraction);
    }

    void visitPremiumPublication(PremiumPublication premiumPublication) {
        // TODO check this
        /*
        Interaction userInteraction = new Interaction();
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(true);
        userInteraction.setArticleId(article.getArticleId());
        userInteraction.setStoreName(article.getStoreName());
        userInteraction.setTags(article.getTags());
        userInteraction.setUserId(userUid);
        interactions.add(userInteraction);
        */
    }

    Task<QuerySnapshot> getArticles() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -14);
        Date dateBefore2Weeks = cal.getTime();
        return FirebaseFirestore.getInstance().collection("articles")
                .whereGreaterThan("creationDate", dateBefore2Weeks)
                .get();
    }

    Task<QuerySnapshot> getPremiumPublications() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -14);
        Date dateBefore2Weeks = cal.getTime();
        return FirebaseFirestore.getInstance().collection("premiumPublications")
                .whereGreaterThan("creationDate", dateBefore2Weeks)
                .get();
    }

    void uploadInteractions() {
        for (Interaction interaction : interactions) {
            FirebaseFirestore.getInstance().collection("interactions").add(interaction);
        }
        interactions.clear();
        for (LocalInteraction localInteraction : currentLocalInteractions) {
            // TODO aplicar en producción
            localDAO.insert(localInteraction);
        }
        currentLocalInteractions.clear();
    }
}