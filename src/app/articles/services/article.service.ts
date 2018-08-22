import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase';
import { Observable } from '../../../../node_modules/rxjs';
import { Article } from '../models/article';



@Injectable()
export class ArticleService {
    articleCollection: AngularFirestoreCollection<Article>;
    articles: Observable<Article>;
    // tslint:disable-next-line:no-inferrable-types
    collectionPath: string = 'articles';
  constructor(public db: AngularFirestore) {
    this.articleCollection = this.db.collection(this.collectionPath);
  }

  addItem(article: Article) {
   return this.articleCollection.add(article);

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
