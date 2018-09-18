import {
    AngularFirestore,
    AngularFirestoreCollection
  } from 'angularfire2/firestore';
  import { Injectable } from '@angular/core';
  import { Observable } from 'rxjs';
  import { map } from 'rxjs/operators';
import { Tags } from '../models/tags';

  @Injectable()
  export class TagsService {
      tagsCollection: AngularFirestoreCollection<Tags>;
      tags: Observable<Tags[]>;
      // tslint:disable-next-line:no-inferrable-types
      collectionPath: string = 'tags';

    constructor(public fst: AngularFirestore) {
      this.tagsCollection = this.fst.collection(this.collectionPath);
      this.tags = this.tagsCollection.snapshotChanges().pipe(map(changes => {
        return changes.map(a => {
          const data = a.payload.doc.data() as Tags;
          return data;
        });
      }));
    }

    getTags() {
      return this.tags;
    }
  }
