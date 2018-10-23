import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase';
import { AngularFirestore } from 'angularfire2/firestore';

@Injectable()
export class AuthService {

  constructor(
    public afAuth: AngularFireAuth,
    public db: AngularFirestore
  ) { }

  emailToRegister: String;

  isEmailAvaible(email) {
    return new Promise<any>((resolve, reject) => {
      firebase.auth().fetchSignInMethodsForEmail(email).then((res) => {
        if (res.length === 0) {
          return resolve(true);
        } else {
          return reject(`Ya existe un usuario registrado con el mail ingresado.`);
        }
      });
    });
  }

  /*checkEmailExistance(email) {
    return new Promise<any>((resolve, reject) => {
        const ref = this.db.collection('stores').ref;
        ref.where('storeMail', '==', email)
            .get()
            .then(snapshot => {
                return resolve(snapshot.empty);
            });
    });
  }*/

  doFacebookLogin() {
    return new Promise<any>((resolve, reject) => {
      const provider = new firebase.auth.FacebookAuthProvider();
      this.afAuth.auth
        .signInWithPopup(provider)
        .then(res => {
          resolve(res);
        }, err => {
          console.log(err);
          reject(err);
        });
    });
  }

  doTwitterLogin() {
    return new Promise<any>((resolve, reject) => {
      const provider = new firebase.auth.TwitterAuthProvider();
      this.afAuth.auth
        .signInWithPopup(provider)
        .then(res => {
          resolve(res);
        }, err => {
          console.log(err);
          reject(err);
        });
    });
  }

  doGoogleLogin() {
    return new Promise<any>((resolve, reject) => {
      const provider = new firebase.auth.GoogleAuthProvider();
      provider.addScope('profile');
      provider.addScope('email');
      this.afAuth.auth
        .signInWithPopup(provider)
        .then(res => {
          resolve(res);
        }, err => {
          console.log(err);
          reject(err);
        });
    });
  }

  doRegister(formControl) {
    return new Promise<any>((resolve, reject) => {
      firebase.auth().createUserWithEmailAndPassword(formControl.controls['email'].value, formControl.controls['password'].value)
        .then(res => {
          console.log(res);
          resolve(res);
        }, err => reject(err));
    });
  }

  doLogin(formControl) {
    return new Promise<any>((resolve, reject) => {
      firebase.auth().signInWithEmailAndPassword(formControl.controls['email'].value, formControl.controls['password'].value)
        .then(res => {
          resolve(res);
        }, err => reject(err + `error en doLogin`));
    });
  }

  doLogout() {
    return new Promise((resolve, reject) => {
      if (firebase.auth().currentUser) {
        this.afAuth.auth.signOut();
        resolve();
      } else {
        reject();
      }
    });
  }

  getEmailToRegister() {
    return this.emailToRegister;
  }


}
