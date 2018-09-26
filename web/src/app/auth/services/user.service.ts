import { Injectable } from '@angular/core';
import { AngularFirestore, AngularFirestoreDocument, AngularFirestoreCollection } from 'angularfire2/firestore';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase';
import 'rxjs/add/operator/mergeMap';
import { Observable } from 'rxjs';
import { FirebaseUserModel } from '../models/user.model';
import { map } from 'rxjs/internal/operators/map';

@Injectable()
export class UserService {
  usersCollection: AngularFirestoreCollection<FirebaseUserModel>;
  userA: Observable<FirebaseUserModel[]>;
  constructor(
   public db: AngularFirestore,
   public afAuth: AngularFireAuth
 ) {

  this.usersCollection = this.db.collection('usuarios', ref => ref.orderBy('userName', 'asc'));
    this.userA = this.usersCollection.snapshotChanges().pipe(map(changes => {
      return changes.map(a => {
        const data = a.payload.doc.data();
        data.firebaseId = a.payload.doc.id;
        return data;
      });
    }));

 }

 checkUserExistance(userName) {
  return new Promise<any>((resolve, reject) => {
      const ref = this.db.collection('usuarios').ref;
      ref.where('userName', '==', userName)
          .get()
          .then(snapshot => {
              return resolve(snapshot.empty);
          });

  });
}

  getCurrentUser() {
    return new Promise<any>((resolve, reject) => {
      // tslint:disable-next-line:no-shadowed-variable
      const user = firebase.auth().onAuthStateChanged(function(user) {
        if (user) {
          resolve(user);
        } else {
          reject('No user logged in');
        }
      });
    });
  }

  getUserInfo(userUid) {
    console.log('ya estoy pidiendo usuario:' + userUid);
    return this.userA.pipe(map(items => items.filter(item => {
      console.log(`este ${item.userId} vs ${userUid}`);
      return item.userId === userUid;
    }
      )));
}


/*
  getUserInfo(userUid) {
      const user = this.db.collection('usuarios', (ref) => ref.where('userId', '==', userUid).limit(1)).valueChanges();
      user.subscribe((userReal) => {
        this.userA = userReal[0] as Observable<FirebaseUserModel>;
      });
      return this.userA;
  }
*/
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
