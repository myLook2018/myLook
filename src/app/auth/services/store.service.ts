import { AngularFirestore, AngularFirestoreCollection } from '../../../../node_modules/angularfire2/firestore';
import { Injectable } from '../../../../node_modules/@angular/core';
import * as firebase from 'firebase';


@Injectable()
export class StoreService {

    constructor(
        public db: AngularFirestore
    ) {
    }

    checkUserExistance(userName) {
        // const ref = this.db.collection('usuarios').ref;
        // return ref.where('userName', '==', userName)
        //     .get()
        //     .then(snapshot => {
        //         return snapshot.empty;
        //     });
    }
}
