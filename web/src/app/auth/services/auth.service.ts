import { Injectable } from '@angular/core';
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

  actionCodeSettings = {
    // URL you want to redirect back to. The domain (www.example.com) for this
    // URL must be whitelisted in the Firebase Console.
    url: 'https://www.example.com/finishSignUp?cartId=1234',
    // This must be true.
    handleCodeInApp: true,
    iOS: {
      bundleId: 'com.example.ios'
    },
    android: {
      packageName: 'com.example.android',
      installApp: true,
      minimumVersion: '12'
    }
};

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

  sendEmailVerification() {
    const user = firebase.auth().currentUser;
    user.sendEmailVerification().then( () => {
      return('¡Se ha enviado un email a su correo electrónico! Es necesario que realice la verificación de la cuenta antes de seguir. ');
    }).catch(function(error) {
      return (error);
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

  doFirstLogin(formControl) {
    return new Promise<any>((resolve, reject) => {
      firebase.auth().signInWithEmailAndPassword(formControl.controls['email'].value, formControl.controls['password'].value)
        .then(res => {
          const isLoged = this.sendEmailVerification(); // no aca
          console.log(isLoged);
          resolve(res);
        }, err => reject(err + `error en doLogin`));
    });
  }

  doLogin(formControl) {
    return new Promise<any>((resolve, reject) => {
      firebase.auth().signInWithEmailAndPassword(formControl.controls['email'].value, formControl.controls['password'].value)
        .then(res => {
          console.log(firebase.auth().currentUser.emailVerified);
          /*if (!firebase.auth().currentUser.emailVerified) {
            this.doLogout();
            reject('no se ha verificado Email');
          }*/
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
