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
@Injectable()
export class DataService {
  task: AngularFireUploadTask;
  ref: AngularFireStorageReference;
  public storeInfo: StoreModel = new StoreModel();
  _subscription: Subscription;
  isNewUser = true;
  constructor(
    private storage: AngularFireStorage,
    public userService: UserService
    ) {}

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
    return new Promise<any>((resolve, reject) => {
      console.log(fileSelected);
      const file = fileSelected.item(0);
      if (file.type.split('/')[0] !== 'image') {
        return reject('Tipo de imagen no soportado');
      }
      const path = `article/${new Date().getTime()}_${file.name}`;
      const customMetadata = { app: 'Mylook!' };
      this.task = this.storage.upload(path, file, { customMetadata });
      this.ref = this.storage.ref(path);
      this.task
        .snapshotChanges()
        .pipe(
          finalize(() => {
            this.ref.getDownloadURL().subscribe(url => {
              this.downloadURL = url;
              console.log(this.downloadURL);
              return resolve(this.downloadURL);
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
    return new Promise<StoreModel>(resolve => {
      console.log(`1)Inicializando refresh information`);
      this.userService.getCurrentUser().then(
        user => {
          console.log(`2)obtuvimos un usuario`);
          this.storeInfo = user;
          console.log('el usuario ', this.storeInfo);
          this.getUserStoreInfo(user.uid).then(storeInfo => {
            console.log(`3)obtuvimos una tienda a partir del usuario`);
            if (storeInfo) {
              console.log(`4)la tienda existe`);
              this.storeInfo = storeInfo;
              this.isNewUser = false;
              resolve(this.storeInfo);
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

  public getStoreInfo() {
    return new Promise<StoreModel>(resolve => {
      if (this.storeInfo.storeName === '') {
        this.refreshLocalUserInformation().then(storeInfo => {
          this.storeInfo = storeInfo;
          resolve(this.storeInfo);
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
}
