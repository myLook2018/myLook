import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Article } from '../models/article';
import { map, filter } from 'rxjs/operators';
import * as firebase from 'firebase';
import { StoreFront } from '../models/storeFront';
import { reject } from 'q';
import { HttpHeaders } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { UserService } from 'src/app/auth/services/user.service';

@Injectable()
export class ArticleService {
  articleCollection: AngularFirestoreCollection<Article>;
  promoteCollection: AngularFirestoreCollection;
  articles: Observable<Article[]>;
  articlesCopado: Article[] = [];
  // tslint:disable-next-line:no-inferrable-types
  collectionPath: string = 'articles';
  storeFrontPath = 'storeFronts';
  promotePath = 'promotions';
  db: any;
  require: any;

  constructor(public fst: AngularFirestore, private http: HttpClient, private userService: UserService) {
    this.articleCollection = this.fst.collection(this.collectionPath);
    this.promoteCollection = this.fst.collection(this.promotePath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  /*getArticles(storeName) {
    console.log(`en el get`);
    return this.articles = this.articleCollection.snapshotChanges().pipe(map(changes => {
      return changes.map(a => {
        const data = a.payload.doc.data();
        data.articleId = a.payload.doc.id;
        console.log(data);
        return data;
      });
    }));
  }*/

  getArticlesCopado(storeName) {
    this.articlesCopado = [];
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por ` + storeName);
      const res = this.db
        .collection(this.collectionPath)
        .where('storeName', '==', storeName)
        .get()
        .then(querySnapshot => {
          querySnapshot.forEach(doc => {
            const data = doc.data();
            data.articleId = doc.id;
            this.articlesCopado.push(data);
          });
        })
        .then(() => {
          resolve(this.articlesCopado);
        })
        .catch(function(error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  getFrontArticlesCopado(storeName) {
    this.articlesCopado = [];
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por ` + storeName);
      const res = this.db
        .collection(this.collectionPath)
        .where('storeName', '==', storeName)
        .where('estaEnVidriera', '==', true)
        .get()
        .then(querySnapshot => {
          querySnapshot.forEach(doc => {
            const data = doc.data();
            data.articleId = doc.id;
            this.articlesCopado.push(data);
          });
        })
        .then(() => {
          resolve(this.articlesCopado);
        })
        .catch(function(error) {
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
    return this.fst
      .collection(this.collectionPath)
      .doc(`${article.articleId}`)
      .delete();
  }

  refreshVidrieraAttribute(articleUID, newValue: boolean) {
    return new Promise<any>(resolve => {
      this.fst
        .collection(this.collectionPath)
        .doc(articleUID)
        .update({
          estaEnVidriera: newValue
        })
        .then(() =>
          resolve(console.log(articleUID + `actualizado a ` + newValue))
        );
    }).catch(error => reject(error));
  }

  refreshArticle(article: Article) {
    return this.fst
      .collection(this.collectionPath)
      .doc(`${article.articleId}`)
      .update({
        cost: article.cost,
        size: article.sizes,
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

  addPromotionToArticle(promotion) {
    return this.fst
      .collection(this.collectionPath)
      .doc(`${promotion.articleId}`)
      .update({
        promotionLevel: promotion.promotionLevel
      })
      .then(() => {
        console.log(`promocion añadida con exito`);
      })
      .catch(() => {
        console.log(`error al cargar la promoció`);
      });
  }

  promoteArticle(data, article: Article, storeUID) {
    console.log('la data', data);
    console.log('el article', article);
    let end = new Date();
    end.setDate(end.getDate() + data.duration);
    const promotion = {
      articleId: article.articleId,
      endOfPromotion: end,
      storeId: storeUID,
      payMethod: data.payMethod,
      promotionLevel: data.promotionLevel,
      promotionCost: data.promotionCost,
      startOfPromotion: data.startOfPromotion
    };
    console.log('la promo en el service', promotion);
    this.addPromotionToArticle(promotion);
    return this.promoteCollection.add(promotion);
  }

  addStoreFront(storeName, articles, storeFrontNumber) {
    const sf = this.getStoreFront(storeName).then(res => {
      if (res !== undefined) {
        console.log(`en el if`);
        this.addSubCollection(res, articles, storeFrontNumber);
      } else {
        console.log(`en el else`);
        this.createStoreFront(storeName).then(() => {
          // this.addStoreFront(storeFirebaseUID, article, storeFrontNumber);
        });
      }
    });
  }

  addSubCollection(doc, articles, frontNumber) {
    this.fst
      .collection(this.storeFrontPath)
      .doc(`${doc.id}`)
      .collection(`storeFronts`)
      .add({
        storeFrontNumber: frontNumber,
        articles: articles.map(x => x)
      });
  }

  getStoreFront(storeName) {
    let lala;
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<StoreFront>((resolve, reject) => {
      this.db
        .collection(this.storeFrontPath)
        .where('storeName', '==', storeName)
        .get()
        .then(querySnapshot => {
          querySnapshot.forEach(doc => {
            const data: StoreFront = doc.data();
            data.id = doc.id;
            console.log(`encontre algo`);
            lala = data;
          });
        })
        .then(() => {
          return resolve(lala);
        })
        .catch(function(error) {
          console.log('Error getting storeFront: ', error);
          reject(error);
        });
      console.log(`lala` + lala);
    });
  }

  createStoreFront(storeName) {
    console.log(`creando`);
    return new Promise<any>(resolve => {
      const res = this.fst.collection(this.storeFrontPath).add({
        storeName: storeName
      });
      res.then(ref => console.log(ref.id));
    });
  }

  /** POST: add a new hero to the database */
  public removeStore(storeIDReal: string, storeNameReal): Observable<string> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.post<string>(
      `https://us-central1-mylook-develop.cloudfunctions.net/recursiveDeleteStore?storeID=${storeIDReal}&storeName=${storeNameReal}`,
      '' , httpOptions
    );
  }
}
