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
            storeName: newDats.storeName,
            storeMail: newDats.storeMail,
            ownerName: newDats.ownerName,
            profilePh: newDats.profilePh,
            coverPh: newDats.coverPh,
            storePhone: newDats.storePhone,
            facebookLink: newDats.facebookLink,
            storeProvince: newDats.storeProvince,
            storeCity: newDats.storeCity,
            storeAddressNumber: newDats.storeAddressNumber,
            storeFloor: newDats.storeFloor,
            storePosition: newDats.storePosition,
            storeDescription: newDats.storeDescription,
            instagramLink: newDats.instagramLink,
            twitterLink: newDats.twitterLink,
            storeAddress: newDats.storeAddress,
        }).then(() => {
                console.log('Document successfully updated!');
            })
            .catch((error) => {
                // The document probably doesn't exist.
                console.error('Error updating document: ', error);
            });
    }
}
