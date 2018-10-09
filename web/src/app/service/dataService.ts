import { AngularFirestore, AngularFirestoreCollection, DocumentReference } from 'angularfire2/firestore';
import { Injectable, Inject } from '@angular/core';
import { UserService } from '../auth/services/user.service';


@Injectable()
export class DataService {
    constructor(
        public db: AngularFirestore,
        public usrService: UserService
    ) {}
}
