import { Injectable } from '@angular/core';
import { AngularFireAuth } from 'angularfire2/auth';
import * as firebase from 'firebase';
import { AngularFirestore } from 'angularfire2/firestore';
import { MatSnackBar } from '@angular/material';

@Injectable()
export class AuthService {

  constructor(
    public snackBar: MatSnackBar,
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
          console.log(`ya esta registrado`);
          return reject(`Ya existe un usuario registrado con el mail ingresado.`);
        }
      }).catch(error => {
        this.openSnackBar(this.translateError(error.code), 'x');
        reject();
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
      // tslint:disable-next-line: max-line-length
      this.openSnackBar('¡Se ha enviado un email a su correo electrónico! Es necesario que realice la verificación de la cuenta antes de seguir. ', 'x')
      return('¡Se ha enviado un email a su correo electrónico! Es necesario que realice la verificación de la cuenta antes de seguir. ');
    }).catch(function(error) {
      this.openSnackBar(this.translateError(error.code), 'x');
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
        }, error => {
          this.openSnackBar(this.translateError(error.code), 'x');
          reject(''); });
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

  sendResetPasswordEmail(email) {
    return new Promise((resolve, reject) => {
    firebase.auth().sendPasswordResetEmail(email).then( res => {
      console.log('se envio el email para resetear');
      this.openSnackBar('Te hemos enviado un email para reestablecer tu contraseña!', 'cerrar');
      // resolve(res);
    }).catch( error => {
      console.log ('ocurrio un error al intentar mandar mail. ', error);
      this.openSnackBar(this.translateError(error.code), 'x');
      // reject(error);
    });
  });
  }

  translateError(error: string) {
    let message = '';
    switch (true) {
      case (error.includes('auth/wrong-password')):
        message = 'Constraseña incorrecta.';
        break;
      case (error.includes('auth/too-many-requests')):
        // tslint:disable-next-line: max-line-length
        message = 'Verifica por favor los datos ingresados. Si los intentos fallidos continúan, bloquearemos temporalmente tu cuenta por seguridad.';
        break;
      case (error.includes('auth/user-not-found')):
        message = 'No se ha encontrado usuario registrado con ese email, verifica los datos ingresados.';
        break;
      case (error.includes('auth/invalid-email')):
        message = 'El email ingresado no es válido.';
        break;
      default:
        message = error;
        break;
    }
    return message;
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 5000
    });
  }
}
