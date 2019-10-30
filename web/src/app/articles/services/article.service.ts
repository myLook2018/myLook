import { AngularFirestore, AngularFirestoreCollection} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Article } from '../models/article';
import { map, filter, catchError } from 'rxjs/operators';
import * as firebase from 'firebase';
import { StoreFront } from '../models/storeFront';
import { reject } from 'q';
import { HttpClient } from '@angular/common/http';
import { PreferenceMP } from '../models/preferenceMP';
import { HttpHeaders } from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable()
export class ArticleService {
  mpURL = 'https://us-central1-app-mylook.cloudfunctions.net/postMercadopagoCheckout';
  articleCollection: AngularFirestoreCollection<Article>;
  promoteCollection: AngularFirestoreCollection;
  articles: Observable<Article[]>;
  articlesCopado: Article[] = [];
  // tslint:disable-next-line:no-inferrable-types
  collectionPath: string = 'articles';
  storeFrontPath = 'storeFronts';
  mercadoPagoPath = 'promotion';
  promotePath = 'promotions';
  storeCollectionPath = 'stores';
  db: any;
  require: any;
  articlesByCode: any[];

  constructor(public fst: AngularFirestore, private http: HttpClient) {
    console.log(`en el collector`);
    this.articleCollection = this.fst.collection(this.collectionPath);
    this.promoteCollection = this.fst.collection(this.promotePath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getArticles(storeName) {
    console.log(`en el get subscriptions`);
    return this.articles = this.articleCollection.snapshotChanges().pipe(map( changes => {
      return changes.map( a => {
        if (a.payload.doc.data().storeName === storeName) {
          const data = a.payload.doc.data();
          console.log(data);
          data.articleId = a.payload.doc.id;
          return data;
        }
      });
    }));
  }

  getArticlesCopado(storeName) {
    this.articlesCopado = [];
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por ` + storeName);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName)
        .get().then((querySnapshot) => {
          this.articlesCopado = [];
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.articleId = doc.id;
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

  getFrontArticlesCopado(storeName) {
    this.articlesCopado = [];
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por ` + storeName);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName).where('isStorefront', '==', true)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.articleId = doc.id;
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
    return this.fst.collection(this.collectionPath).doc(`${article.articleId}`).delete();
  }

  refreshVidrieraAttribute(articleUID, newValue: boolean) {
    return new Promise<any> ((resolve) => {
      this.fst.collection(this.collectionPath).doc(articleUID).update({
        isStorefront: newValue
      }).then(() => resolve(console.log(articleUID + `actualizado a ` + newValue)));
    }).catch(error => reject(error));
  }

  refreshArticle(article: Article, articleId) {
    return this.fst.collection(this.collectionPath).doc(articleId).update(article)
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

  promoteArticle(data, article: Article, storeUID) {
    console.log('la data', data);
    console.log('el article', article);
    const end = new Date;
    end.setDate(end.getDate() + data.duration);
    const promotion = {
      articleId: article.articleId,
      endOfPromotion: end,
      storeId: storeUID,
      payMethod: data.payMethod,
      promotionLevel: data.promotionLevel,
      promotionCost: data.promotionCost,
      startOfPromotion: data.startOfPromotion,
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
    this.fst.collection(this.storeFrontPath).doc(`${doc.id}`).collection(`storeFronts`).add({
      storeFrontNumber: frontNumber,
      articles: articles.map(x => x)
    });
  }

  getStoreFront(storeName) {
    let lala;
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<StoreFront>((resolve, reject) => {
      this.db.collection(this.storeFrontPath).where('storeName', '==', storeName)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data: StoreFront = doc.data();
            data.id = doc.id;
            console.log(`encontre algo`);
            lala = data;
          });
        }).then(() => {
          return resolve(lala);
        })
        .catch(function (error) {
          console.log('Error getting storeFront: ', error);
          reject(error);
        });
      console.log(`lala` + lala);
    });
  }

  createStoreFront(storeName) {
    console.log(`creando`);
    return new Promise<any>((resolve) => {
      const res = this.fst.collection(this.storeFrontPath).add({
        storeName: storeName,
      });
      res.then(ref => console.log(ref.id));
    });
  }

  createNewSale(preferenceMP) {
    console.log('creando venta');
    return new Promise<any>((resolve) => {
      const res = this.fst.collection(this.mercadoPagoPath).add(preferenceMP);
      res.then(ref => console.log(ref.id));
      resolve(res);
    });
  }

  tryPromoteMP(preferenceMP)  {
    return this.http.post(this.mpURL, preferenceMP);
  }

  getSingleArticle(articleId) {
    console.log('getting article:', articleId);
    // tslint:disable-next-line:no-shadowed-variable
    const docRef = this.db.collection(this.collectionPath).doc(articleId);
    return new Promise<Article>((resolve, reject) => {
      docRef.get().then( document => {
        if (document.exists) {
          resolve(document.data());
        } else {
          reject ('No encontramos documento ' + articleId);
        }
        }).catch(function (error) {
          console.log('Error getting storeFront: ', error);
          reject(error);
        });
    });
  }

  updateStorefront(storeId, storefrontsNew) {
    return this.fst.collection(this.storeCollectionPath).doc(storeId).update(({
      storefronts: storefrontsNew
      })).then(function () {
        console.log('actualizados los storeFronts');
      }).catch(function (error) {
        // The document probably doesn't exist.
        console.error('Error updating document: ', error);
      });
  }

  getArticleByCode( storeName, articleCode ) {
    this.articlesByCode = [];
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por articleCode: ` + articleCode);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            console.log('docs parcial ', data);
            data.articleId = doc.id;
            if ( data.code === articleCode ) {
              console.log('entro este ------------------------------------------');
              this.articlesByCode.push(data);
            }
          });
        }).then(() => {
          resolve(this.articlesByCode);
        })
        .catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }
}
