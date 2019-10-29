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
  campaingsPath = 'voucherCampaing';
  db: any;
  require: any;
  isVisitsCached: boolean;
  isInteractionsCached: boolean;
  isSubsCached = false;
  isRecoCached = false;
  isPromotedCached = false;
  isCampaingsCached = false;
  campaings = [];

  constructor(public fst: AngularFirestore) {
    console.log(`en el collector de analytics`);
    this.interactionCollection = this.fst.collection(this.collectionPath);
    this.visitsCollection = this.fst.collection(this.visitsPath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getInteractions(storeName) {
    console.log(`geting interactions`);
    return new Promise<any>((resolve, reject) => {
      if (!this.isInteractionsCached) {
        console.log(`estamos preguntando interacciones de ` + storeName);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName).orderBy('interactionTime')
      .get().then(queryRes => {
        queryRes.forEach(doc => {
          const data = doc.data();
          data.id = doc.id;
          this.interactions.push(data);
        });
      }).then(() => {
          this.isInteractionsCached = true;
          resolve(this.interactions);
        }).catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
      } else {
        console.log('devolviendo cache');
        resolve(this.interactions);
      }
    });
  }

  getVisits(storeName) {
    console.log(`geting visits`);
    return new Promise<any>((resolve, reject) => {
      if (!this.isVisitsCached) {
        console.log(`estamos preguntando visits de ` + storeName);
        const res = this.db.collection(this.visitsPath).where('storeName', '==', storeName)
        .get().then(queryRes => {
          queryRes.forEach(doc => {
            const data = doc.data();
            data.id = doc.id;
            this.visits.push(data);
          });
        }).then(() => {
            this.isVisitsCached = true;
            resolve(this.visits);
          }).catch(function (error) {
            console.log('Error getting documents: ', error);
            reject(error);
          });
      } else {
        console.log('devolviendo cache visits');
        resolve(this.visits);
      }
    });
  }

  getSubscriptions(storeName) {
    console.log(`geting subscriptions`);
    return new Promise<any>((resolve, reject) => {
      if (!this.isSubsCached) {
        console.log(`estamos preguntando subcripciones de ` + storeName);
        const res = this.db.collection(this.subPath).where('storeName', '==', storeName)
        .get().then(queryRes => {
          queryRes.forEach(doc => {
            const data = doc.data();
            data.id = doc.id;
            this.subscriptions.push(data);
          });
        }).then(() => {
            this.isSubsCached = true;
            resolve(this.subscriptions);
          }).catch(function (error) {
            console.log('Error getting documents: ', error);
            reject(error);
          });
      } else {
        console.log('devolviendo subs cached');
        resolve(this.subscriptions);
      }
    });
  }

  getRecomendationFeedBack(storeName) {
    console.log(`geting Feedback`);
    return new Promise<any>((resolve, reject) => {
      if (!this.isRecoCached) {
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
           this.isRecoCached = true;
            resolve(this.answeredRecomendations);
          }).catch(function (error) {
            console.log('Error getting documents: ', error);
            reject(error);
          });
      } else {
        console.log('devolviendo reco cached');
        resolve(this.answeredRecomendations);
      }
    });
  }

  getPromotedArticles(storeID) {
    console.log(`geting promotions`);
    return new Promise<any>((resolve, reject) => {
      if (!this.isPromotedCached) {
        console.log(`estamos preguntando promociones de ` + storeID);
        const res = this.db.collection(this.promotionsPath).where('storeId', '==', storeID)
        .get().then(queryRes => {
          queryRes.forEach(doc => {
            const data = doc.data();
            data.id = doc.id;
            this.promotions.push(data);
          });
        }).then(() => {
            this.isPromotedCached = true;
            resolve(this.promotions);
          }).catch(function (error) {
            console.log('Error getting documents: ', error);
            reject(error);
          });
        } else {
        console.log('devolviendo promoted cached');
        resolve(this.promotions);
      }
    });
  }

  cleanCache() {
    this.interactions = [];
    this.visits = [];
    this.subscriptions = [];
    this.answeredRecomendations = [];
    this.promotions = [];
    this.isRecoCached = false;
    this.isInteractionsCached = false;
    this.isSubsCached = false;
    this.isVisitsCached = false;
    this.isPromotedCached = false;
  }

  getVoucherCampaings( storeId ) {
    console.log('Obteniendo Campañas de cupones');
    return new Promise<any>((resolve, reject) => {
      if (!this.isCampaingsCached) {
        console.log(`estamos preguntando campañas de cupones de ` + storeId);
        const res = this.db.collection(this.campaingsPath).where('storeId', '==', storeId)
        .get().then(queryRes => {
          queryRes.forEach(doc => {
            const data = doc.data();
            data.id = doc.id;
            if ( data.idMercadoPago ) {
              this.campaings.push(data);
            }
          });
        }).then(() => {
            this.isCampaingsCached = true;
            resolve(this.campaings);
          }).catch(function (error) {
            console.log('Error getting documents: ', error);
            reject(error);
          });
        } else {
        console.log('devolviendo capaings cached');
        resolve(this.campaings);
      }
    });
  }
}
