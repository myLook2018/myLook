import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase';
import { Interaction } from '../../model/interaction';

@Injectable()
export class AnyliticService {
  interactionCollection: AngularFirestoreCollection<Interaction>;
  interactions: Interaction[] = [];
  // tslint:disable-next-line:no-inferrable-types
  collectionPath: string = 'interactions';
  db: any;
  require: any;

  constructor(public fst: AngularFirestore) {
    console.log(`en el collector de interactions`);
    this.interactionCollection = this.fst.collection(this.collectionPath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getInteractions(storeName) {
    console.log(`geting interactions`);
    this.interactions = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos preguntando interacciones de ` + storeName);
      const res = this.db.collection(this.collectionPath).where('storeName', '==', storeName).get().then(queryRes => {
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
}
