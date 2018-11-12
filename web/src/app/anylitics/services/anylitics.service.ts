import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase';
import { Interaction } from '../model/interaction';
import { Visit } from '../model/visit';

@Injectable()
export class AnyliticService {
  interactionCollection: AngularFirestoreCollection<Interaction>;
  visitsCollection: AngularFirestoreCollection<Visit>;
  interactions: Interaction[] = [];
  visits: Visit[] = [];

  // tslint:disable-next-line:no-inferrable-types
  collectionPath: string = 'interactions';
  visitsPath = 'visits';
  db: any;
  require: any;

  constructor(public fst: AngularFirestore) {
    console.log(`en el collector de interactions`);
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
}
