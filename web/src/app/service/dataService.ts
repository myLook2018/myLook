import { AngularFirestore, AngularFirestoreCollection, DocumentReference } from 'angularfire2/firestore';
import { Injectable, Inject } from '@angular/core';
import { UserService } from '../auth/services/user.service';
import { AngularFireUploadTask, AngularFireStorage, AngularFireStorageReference } from 'angularfire2/storage';
import { finalize } from 'rxjs/operators';


@Injectable()
export class DataService {
    task: AngularFireUploadTask;
    ref: AngularFireStorageReference;
    downloadURL;
    constructor(
        private storage: AngularFireStorage,
        public usrService: UserService
    ) { }

    uploadPictureFile(fileSelected) {
        return new Promise<any>((resolve, reject) => {
            console.log(fileSelected);
            const file = fileSelected;
            if (file.type.split('/')[0] !== 'image') {
                return reject('Tipo de imagen no soportado');
            }
            const path = `articles/${new Date().getTime()}_${file.name}`;
            const customMetadata = { app: 'Mylook!' };
            this.task = this.storage.upload(path, file, { customMetadata });
            this.ref = this.storage.ref(path);
            this.task.snapshotChanges().pipe(
                finalize(() => {
                    this.ref.getDownloadURL().subscribe(url => {
                        this.downloadURL = url;
                        console.log(this.downloadURL);
                        return resolve(this.downloadURL);
                    });
                })
                ).subscribe();
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
            this.task.snapshotChanges().pipe(
                finalize(() => {
                    this.ref.getDownloadURL().subscribe(url => {
                        this.downloadURL = url;
                        console.log(this.downloadURL);
                        return resolve(this.downloadURL);
                    });
                })
                ).subscribe();
        });
    }

}
