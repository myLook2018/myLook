import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import {  AngularFireStorage } from 'angularfire2/storage';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Article } from '../models/article';
import { map } from 'rxjs/operators';



@Injectable()
export class ArticleService {
    articleCollection: AngularFirestoreCollection<Article>;
    articles: Observable<Article[]>;
    // tslint:disable-next-line:no-inferrable-types
    collectionPath: string = 'articles';

  constructor(public fst: AngularFirestore, private storage: AngularFireStorage) {
    this.articleCollection = this.fst.collection(this.collectionPath, ref => ref.orderBy('tags', 'asc'));
    this.articles = this.articleCollection.snapshotChanges().pipe(map(changes => {
      return changes.map(a => {
        const data = a.payload.doc.data();
        data.id = a.payload.doc.id;
        return data;
      });
    }));
  }

  addArticle(article: Article) {
   return this.articleCollection.add(article);
  }

  getArticles() {
    return this.articles;
  }

  deleteArticle(article: Article) {
   return this.fst.collection(this.collectionPath).doc(`${article.id}`).delete();
  }

  refreshArticle(article: Article) {
    return this.fst.collection(this.collectionPath).doc(`${article.id}`).update({
      cost: article.cost,
      size: article.size,
      material: article.material,
      colors: article.colors,
      initial_stock: article.initial_stock,
      provider: article.provider,
      tags: article.tags
    })
    .then(function() {
        console.log('Document successfully updated!');
    })
    .catch(function(error) {
        // The document probably doesn't exist.
        console.error('Error updating document: ', error);
    });
  }
}
