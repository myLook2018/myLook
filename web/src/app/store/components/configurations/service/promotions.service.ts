import { AngularFirestore, AngularFirestoreCollection} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import * as firebase from 'firebase';
import { HttpClient } from '@angular/common/http';
import { Promotion } from '../model/promotion';

@Injectable()
export class PromotionsService {
  mpURL = 'https://us-central1-app-mylook.cloudfunctions.net/postMercadopagoCheckout';
  promotionsCollection: AngularFirestoreCollection<Promotion>;
  promotions: Promotion[] = [];
  // tslint:disable-next-line:no-inferrable-types
  promotionsPath: string = 'promotions';
  db: any;
  require: any;

  constructor(public fst: AngularFirestore, private http: HttpClient) {
    console.log(`en el collector`);
    this.promotionsCollection = this.fst.collection(this.promotionsPath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getArticlesCopado(storeId) {
    this.promotions = [];
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando por este ID` + storeId);
      this.db.collection(this.promotionsPath).where('storeId', '==', storeId)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.documentId = doc.id;
            this.promotions.push(data);
          });
        }).then(() => {
          resolve(this.promotions);
        })
        .catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }
}
