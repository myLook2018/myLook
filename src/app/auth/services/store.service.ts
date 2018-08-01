import { AngularFirestore, AngularFirestoreCollection } from '../../../../node_modules/angularfire2/firestore';
import { Injectable } from '../../../../node_modules/@angular/core';
import * as firebase from 'firebase';


@Injectable()
export class StoreService {

    constructor(
        public db: AngularFirestore
    ) {
    }

    checkUserExistance(user) {
        return new Promise<any>((resolve, reject) => {
            // tslint:disable-next-line:no-shadowed-variable
            const ref = this.db.collection('usuarios').ref;
            console.log(ref)
            this.db.collection("usuarios").ref
            ref.where('userName', '==', user.userName)
                .get()
                .then(snapshot => {
                    console.log(snapshot.empty)
                    return snapshot.empty
                });

        });


    }
}
