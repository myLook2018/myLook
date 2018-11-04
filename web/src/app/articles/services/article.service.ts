import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Article } from '../models/article';
import { map, filter } from 'rxjs/operators';
import * as firebase from 'firebase';

@Injectable()
export class ArticleService {
  articleCollection: AngularFirestoreCollection<Article>;
  promoteCollection: AngularFirestoreCollection;
  articles: Observable<Article[]>;
  articlesCopado: Article[] = [];
  // tslint:disable-next-line:no-inferrable-types
  collectionPath: string = 'articles';
  promotePath = 'promotions';
  db: any;
  require: any;

  constructor(public fst: AngularFirestore) {
    console.log(`en el collector`);
    this.articleCollection = this.fst.collection(this.collectionPath);
    this.promoteCollection = this.fst.collection(this.promotePath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getArticles(storeName) {
    console.log(`en el get`);
    return this.articles = this.articleCollection.snapshotChanges().pipe(map(changes => {
      return changes.map(a => {
        const data = a.payload.doc.data();
        data.id = a.payload.doc.id;
        console.log(data);
        return data;
      });
    }));
  }

  getArticlesCopado(storeName) {
    this.articlesCopado = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por ` + storeName);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.id = doc.id;
            this.articlesCopado.push(data);
          });
        }).then(() => {
          resolve(this.articlesCopado);
        })
        .catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  addArticle(article: Article) {
    console.log(article);
    return this.articleCollection.add(article);
  }

  deleteArticle(article: Article) {
    return this.fst.collection(this.collectionPath).doc(`${article.id}`).delete();
  }

  refreshArticle(article: Article) {
    return this.fst.collection(this.collectionPath).doc(`${article.id}`).update({
      cost: article.cost,
      size: article.sizes,
      material: article.material,
      colors: article.colors,
      initial_stock: article.initial_stock,
      provider: article.provider,
      tags: article.tags
    })
      .then(function () {
        console.log('Document successfully updated!');
      })
      .catch(function (error) {
        // The document probably doesn't exist.
        console.error('Error updating document: ', error);
      });
  }

  addPromotionToArticle(promotion) {
    return this.fst.collection(this.collectionPath).doc(`${promotion.articleId}`).update({
      promotionLevel: promotion.promotionLevel
    }).then(() => { console.log(`promocion añadida con exito`); })
      .catch(() => { console.log(`error al cargar la promoció`); })
      ;
  }

  promoteArticle(data, article) {
    const promotion = {
      articleId: article.id,
      endOfPromotion: data.dueDate,
      storeName: article.storeName,
      promotionLevel: data.promotionLevel,
      payMethod: data.payMethod,
      dailyCost: data.dailyCost
    };
    console.log(promotion);
    this.addPromotionToArticle(promotion);
    return this.promoteCollection.add(promotion);
  }
}
