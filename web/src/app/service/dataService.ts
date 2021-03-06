import { Injectable, Inject } from '@angular/core';
import { UserService } from '../auth/services/user.service';
import {
  AngularFireUploadTask,
  AngularFireStorage,
  AngularFireStorageReference
} from 'angularfire2/storage';
import { finalize, delay } from 'rxjs/operators';
import { StoreModel } from '../auth/models/store.model';
import { Subscription } from 'rxjs';
import * as firebase from 'firebase';
import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
@Injectable()
export class DataService {
  task: AngularFireUploadTask;
  ref: AngularFireStorageReference;
  public storeInfo: StoreModel = new StoreModel();
  userFirebase;
  _subscription: Subscription;
  isNewUser = true;
  db: firebase.firestore.Firestore;
  voucherCollection: AngularFirestoreCollection;
  constructor(
    private storage: AngularFireStorage,
    public fst: AngularFirestore,
    public userService: UserService
    ) {
      this.db = firebase.firestore();
      this.voucherCollection = this.fst.collection('voucherCampaing');

    }

    uploadPictureFile(fileSelected) {
      return new Promise<any>((resolve, reject) => {
      const file = fileSelected;
      if (file.type.split('/')[0] !== 'image') {
        return reject('Tipo de imagen no soportado');
      }
      const path = `articles/${new Date().getTime()}_${file.name}`;
      const customMetadata = { app: 'Mylook!' };
      this.task = this.storage.upload(path, file, { customMetadata });
      this.ref = this.storage.ref(path);
      this.task
        .snapshotChanges()
        .pipe(
          finalize(() => {
            this.ref.getDownloadURL().subscribe(url => {
              console.log(url);
              return resolve(url);
            });
          })
        )
        .subscribe();
    });
  }

  uploadPicture(fileSelected: FileList) {
    console.log(FileList);
    return new Promise<any>((resolve, reject) => {
      console.log(fileSelected);
      const file = fileSelected.item(0);
      if (file.type.split('/')[0] !== 'image') {
        return reject('Tipo de imagen no soportado');
      }
      const path = `articles/${new Date().getTime()}_${file.name}`;
      const customMetadata = { app: 'Mylook!' };
      this.task = this.storage.upload(path, file, { customMetadata });
      this.ref = this.storage.ref(path);
      this.task
        .snapshotChanges()
        .pipe(
          finalize(() => {
            this.ref.getDownloadURL().subscribe(url => {
              console.log(url);
              return resolve(url);
            });
          })
        )
        .subscribe();
    });
  }

  private getUserStoreInfo(userUID) {
    return new Promise<StoreModel>(resolve => {
      this._subscription = this.userService
        .getUserInfo(userUID)
        .subscribe(userA => {
          resolve(userA[0]);
        });
    });
  }

  public refreshLocalUserInformation() {
    return new Promise<StoreModel>((resolve, reject) => {
      console.log(`1)Inicializando refresh information`);
      this.userService.getCurrentUser().then(
        user => {
          console.log('current user: ', user);
          console.log(`2)obtuvimos un usuario`);
          this.userFirebase = user;
          console.log('el usuario ', this.userFirebase);
          this.getUserStoreInfo(user.uid).then(storeInfo => {
            console.log(`3)obtuvimos una tienda a partir del usuario`);
            if (storeInfo) {
              console.log(`4)la tienda existe`);
              this.storeInfo = storeInfo;
              this.isNewUser = false;
              resolve(this.storeInfo);
            } else {
              console.log(' la tienda no existe, a crearla');
              reject(user.email);
            }
            console.log(5);
          });
        },
        error => {
          console.log('No se registro usuario loggeado');
          resolve(this.storeInfo);
        }
      );
    });
  }

  public getStoreInfo(force: boolean = false) {
    return new Promise<StoreModel>((resolve, reject) => {
      if (this.storeInfo.storeName || force) {
        this.refreshLocalUserInformation().then(storeInfoa => {
          this.storeInfo = storeInfoa;
          resolve(this.storeInfo);
        }, error => {
          console.log('hay usuario pero no hay tienda, que cagada macho, a crearla');
          reject(error);
        });
      } else {
        console.log(`PASAMOS EL USUARIO LOCAL`, this.storeInfo);
        resolve(this.storeInfo);
      }
    });
  }

  public cleanCache() {
    this.storeInfo = new StoreModel();
    this.isNewUser = true;
    this._subscription.unsubscribe();
    return this.storeInfo;
  }

  getFirebaseUser() {
    return this.userFirebase;
  }

  getNumberClients() {
    const clients = [];
    return new Promise<any>((resolve, reject) => {
      this.db.collection('clients')
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.id = doc.id;
            clients.push(data);
          });
        }).then(() => {
          return resolve(clients);
        })
        .catch(function (error) {
          console.log('Error getting clients: ', error);
          reject(error);
        });
      console.log(`clients ` + clients);
    });
  }

  getNumberOfSubscriptors() {
    const subscritors = [];
    return new Promise<any>((resolve, reject) => {
      this.db.collection('subscriptions').where('storeName', '==', this.storeInfo.storeName)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.id = doc.id;
            subscritors.push(data);

          });
        }).then(() => {
          return resolve(subscritors);
          // asyncForEach();
          // this.getClientDocIdFromUserID(data.userId).then(docId => {
          //   data.clientDocumentId = docId;
          // });
        })
        .catch(function (error) {
          console.log('Error getting clients: ', error);
          reject(error);
        });
      console.log(`subscritors ` + subscritors);
    });
  }

  addNewVoucherCollection(voucherData) {
    console.log(voucherData);
    return this.voucherCollection.add(voucherData);
  }

  tryActivateVoucher( voucherCode ) {
    let result = {
      title: '',
      usedDate: ''
    };
    return new Promise<any>((resolve, reject) => {
      this.db.collection('vouchers').doc(voucherCode)
        .get().then((doc) => {
          if (doc.exists) {
            result.title = doc.data().usedDate ? 'used' : 'success';
            if (result.title === 'success') {
              doc.ref.update({usedDate: new Date, used: true });
            } else {
              result.usedDate = doc.data().usedDate;
            }
          } else {
            result.title = 'invalid';
          }
        }).then(() => {
          return resolve(result);
        })
        .catch(function (error) {
          console.log('Error getting clients: ', error);
          reject(error);
        });
      console.log(`y el resultado fue ` + result);
    });
  }

  getClientDocIdFromUserID( userIdRequired ) {
    let clientDocID = '';
    return new Promise<any>((resolve, reject) => {
      this.db.collection('clients').where('userId', '==', userIdRequired)
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.id = doc.id;
            clientDocID = data.id;
          });
        }).then(() => {
          return resolve(clientDocID);
        })
        .catch(function (error) {
          console.log('Error getting clients: ', error);
          reject(error);
        });
      console.log(`subscritors ` + clientDocID);
    });
  }
}
