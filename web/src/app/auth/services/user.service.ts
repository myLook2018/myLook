import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreDocument, AngularFirestoreCollection } from 'angularfire2/firestore';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase';
import 'rxjs/add/operator/mergeMap';
import { Observable } from 'rxjs';
import { StoreModel } from '../models/store.model';
import { map } from 'rxjs/internal/operators/map';
import { AuthService } from './auth.service';

@Injectable()
export class UserService {
  storesCollection: AngularFirestoreCollection<StoreModel>;
  storeA: Observable<StoreModel[]>;
  constructor(
    public db: AngularFirestore,
    public afAuth: AuthService
  ) {

    this.storesCollection = this.db.collection('stores', ref => ref.orderBy('storeName', 'asc'));
    this.storeA = this.storesCollection.snapshotChanges().pipe(map(changes => {
      return changes.map(a => {
        const data = a.payload.doc.data();
        data.firebaseUID = a.payload.doc.id;
        return data;
      });
    }));

  }

  addStore(userStore: StoreModel) {
    return this.storesCollection.add(userStore);
  }

  checkUserExistance(storeName) {
    return new Promise<any>((resolve, reject) => {
      const ref = this.db.collection('stores').ref;
      ref.where('storeName', '==', storeName)
        .get()
        .then(snapshot => {
          console.log(snapshot.empty);
          return resolve(snapshot.empty);
        });

    });
  }

  getCurrentUser() {
    return new Promise<any>((resolve, reject) => {
      // tslint:disable-next-line:no-shadowed-variable
      const user = firebase.auth().onAuthStateChanged(function (user) {
        if (user) {
          resolve(user);
        } else {
          reject('No user logged in');
        }
      });
    });
  }

  getUserInfo(storeUid) {
    return this.storeA.pipe(map(items => items.filter(item => {
      return item.firebaseUserId === storeUid;
    }
    )));
  }

  updateCurrentUser(value) {
    return new Promise((resolve, reject) => {
      const user = firebase.auth().currentUser;
      user.updateProfile({
        displayName: value.name,
        photoURL: user.photoURL
      }).then((res: any) => {
        resolve(res);
      }, err => reject(err));
    });
  }
}
