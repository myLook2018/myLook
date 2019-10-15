import {
    AngularFirestore,
    AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import * as firebase from 'firebase';
import { StoreModel } from 'src/app/auth/models/store.model';

@Injectable()
export class NewStoreService {
    // tslint:disable-next-line:no-inferrable-types
    collectionPath: string = 'stores';
    db: any;
    require: any;
    storeCollection: AngularFirestoreCollection<{}>;

    constructor(public fst: AngularFirestore) {
        console.log(`en el collector`);
        this.storeCollection = this.fst.collection(this.collectionPath);
        // Required for side-effects
        this.db = firebase.firestore();
    }

    refreshStore(firebaseUID, newDats: StoreModel) {
        return this.fst.collection(this.collectionPath).doc(`${firebaseUID}`).update({
            profilePh: newDats.profilePh,
            coverPh: newDats.coverPh,
            storeLongitude: newDats.storeLongitude,
            storeLatitude: newDats.storeLatitude,
            storeAddress: newDats.storeAddress,
            storeTower: newDats.storeTower,
            storeFloor: newDats.storeFloor,
            storeDept: newDats.storeDept,
            storePhone: newDats.storePhone,
            facebookLink: newDats.facebookLink,
            instagramLink: newDats.instagramLink,
            twitterLink:  newDats.twitterLink,
            storeDescription: newDats.storeDescription
        }).then(() => {
                console.log('Se actualizo la tienda!');
            })
            .catch((error) => {
                // The document probably doesn't exist.
                console.error('Error updating document: ', error);
            });
    }
}
