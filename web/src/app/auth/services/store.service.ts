import { AngularFirestore, AngularFirestoreCollection } from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase';


@Injectable()
export class StoreService {

    constructor(
        public db: AngularFirestore
    ) {
    }

    checkUserExistance(user) {
        return new Promise<any>((resolve, reject) => {
            const ref = this.db.collection('usuarios').ref;
            console.log(ref);
            ref.where('userName', '==', user.userName)
                .get()
                .then(snapshot => {
                    console.log(snapshot.empty);
                    return resolve(snapshot.empty);
                });

        });


    }

    checkStoreExistance(store) {
        return new Promise<any>((resolve, reject) => {
            const ref = this.db.collection('stores').ref;
            ref.where('storeName', '==', store)
                .get()
                .then(snapshot => {
                    return resolve(snapshot.empty);
                });

        });
    }

    tryRegisterStore(usuario, tienda) {
        return new Promise<any>((resolve, reject) => {
            const ref = this.db.collection('usuarios').ref;
            ref.add(usuario).then(snapshot => {
                const store = this.db.collection('stores').ref;
                store.add(tienda).then(storeSnapshot => {
                    return resolve(storeSnapshot.collection);
                });
            });
        });
    }
}
