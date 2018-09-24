import { AngularFirestore, AngularFirestoreCollection, DocumentReference } from 'angularfire2/firestore';
import { Injectable, Inject } from '@angular/core';
import { Store } from '../../store/model/store.model';

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
                    return resolve(snapshot.empty);
                });

        });


    }

    checkStoreExistance(userName) {
        return new Promise<any>((resolve, reject) => {
            const ref = this.db.collection('stores').ref;
            ref.where('userName', '==', userName)
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

    getStoreData(userName): Promise<any> {
        if (this.checkStoreExistance(userName)) {
            return new Promise<any>((resolve, reject) => {
                const ref = this.db.collection('stores').ref;
                ref.where('userName', '==', userName)
                    .get()
                    .then(snapshot => {
                        if (snapshot.docs[0]) {
                            return resolve(snapshot.docs[0].data());
                        } else {
                            return reject('Page not Found');
                        }
                    });
            });
        }
    }

    getStoreArticles(storeName): Promise<any> {
        if (this.checkStoreExistance(storeName)) {
            return new Promise<any>((resolve, reject) => {
                const ref = this.db.collection('articles').ref;
                ref.where('storeName', '==', storeName)
                    .get()
                    .then(snapshot => {
                        if (!snapshot.empty) {
                            return resolve(snapshot.docs);
                        } else {
                            return resolve([]);
                        }
                    });
            });
        }
    }

    editStoreInformation(data: Store): Promise<any> {
        return new Promise<any>((resolve, reject) => {
            const ref = this.db.collection('stores').ref;
            let storeRef: DocumentReference;
            const storeLink = ref.where('userName', '==', data.userName).get().then(result => {
                result.forEach(doc => {
                    storeRef = doc.ref;
                });
                storeRef.set({
                    storeName: data.storeName,
                    storeAddress: data.storeAddress,
                    storeDescription: data.storeDescription,
                    storeFloor: data.storeFloor,
                    storeMail: data.storeMail,
                    storePhone: data.storePhone,
                    storePosition: data.storePosition,
                    userName: data.userName,
                    storeAddressNumber: data.storeAddressNumber,
                    facebookLink: data.facebookLink,
                    twitterlink: data.twitterLink,
                    instagramLink: data.instagramLink
                }).then(error => {
                    if (error) {
                        return reject('Update failed' + error);
                    } else {
                        return resolve(error);
                    }
                });

            });


        });

    }
}
