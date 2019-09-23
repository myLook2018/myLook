import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase';
import { Interaction } from '../model/interaction';
import { Visit } from '../model/visit';
import { Subcription } from '../model/subcription';
import { AnsweredRecom } from '../model/answeredRecom';
import { PromotedArticle } from 'src/app/articles/models/promotedArticle';

@Injectable()
export class AnyliticService {
  interactionCollection: AngularFirestoreCollection<Interaction>;
  visitsCollection: AngularFirestoreCollection<Visit>;
  interactions: Interaction[] = [];
  visits: Visit[] = [];
  subscriptions: Subcription[] = [];
  answeredRecomendations: AnsweredRecom[] = [];
  promotions: PromotedArticle[] = [];

  // tslint:disable-next-line:no-inferrable-types
  collectionPath: string = 'interactions';
  visitsPath = 'visits';
  subPath = 'subscriptions';
  answeredRecomPath = 'answeredRecommendations';
  promotionsPath = 'promotions';
  db: any;
  require: any;

  constructor(public fst: AngularFirestore) {
    console.log(`en el collector de analytics`);
    this.interactionCollection = this.fst.collection(this.collectionPath);
    this.visitsCollection = this.fst.collection(this.visitsPath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getInteractions(storeName) {
    console.log(`geting interactions`);
    this.interactions = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando interacciones de ` + storeName);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName).orderBy('interactionTime')
      .get().then(queryRes => {
        queryRes.forEach(doc => {
          const data = doc.data();
          data.id = doc.id;
          this.interactions.push(data);
        });
      }).then(() => {
          resolve(this.interactions);
        }).catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  getVisits(storeName) {
    console.log(`geting visits`);
    this.visits = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando visits de ` + storeName);
      const res = this.db.collection(this.visitsPath).where('storeName', '==', storeName)
      .get().then(queryRes => {
        queryRes.forEach(doc => {
          const data = doc.data();
          data.id = doc.id;
          this.visits.push(data);
        });
      }).then(() => {
          resolve(this.visits);
        }).catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  getSubscriptions(storeName) {
    console.log(`geting subscriptions`);
    this.subscriptions = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando subcripciones de ` + storeName);
      const res = this.db.collection(this.subPath).where('storeName', '==', storeName)
      .get().then(queryRes => {
        queryRes.forEach(doc => {
          const data = doc.data();
          data.id = doc.id;
          this.subscriptions.push(data);
        });
      }).then(() => {
          resolve(this.subscriptions);
        }).catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  getRecomendationFeedBack(storeName) {
    console.log(`geting Feedback`);
    this.answeredRecomendations = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando feedback de ` + storeName);
      const res = this.db.collection(this.answeredRecomPath).where('storeName', '==', storeName)
      .get().then(queryRes => {
        queryRes.forEach(doc => {
          const data = doc.data();
          data.id = doc.id;
          this.answeredRecomendations.push(data);
        });
      }).then(() => {
         console.log('feedback que devolvemos', this.answeredRecomendations);
          resolve(this.answeredRecomendations);
        }).catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  getPromotedArticles(storeID) {
    console.log(`geting promotions`);
    this.promotions = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando promociones de ` + storeID);
      const res = this.db.collection(this.promotionsPath).where('storeId', '==', storeID)
      .get().then(queryRes => {
        queryRes.forEach(doc => {
          const data = doc.data();
          data.id = doc.id;
          this.promotions.push(data);
        });
      }).then(() => {
          resolve(this.promotions);
        }).catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }
}
