package com.mylook.mylook.entities;

import java.io.Serializable;
import java.sql.Timestamp;

public class Favorite implements Serializable {
    private String articleId;
    private String collection;
    private String downloadUri;

    public Favorite() {
    }

    public Favorite(String articleId, String collection, String downloadUri) {
        this.articleId = articleId;
        this.collection = collection;
        this.downloadUri = downloadUri;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }
}
