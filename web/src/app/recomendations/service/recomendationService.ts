import {
    AngularFirestore,
    AngularFirestoreCollection
  } from 'angularfire2/firestore';
  import {  AngularFireStorage } from 'angularfire2/storage';
  import { Injectable } from '@angular/core';
  import { Observable } from 'rxjs';
  import { map } from 'rxjs/operators';
import { RecomendationRequest } from '../model/recomendationRequest.model';
import { RecomendationAnswer } from '../model/recomendationAnswer.model';

  @Injectable()
  export class ArticleService {
      recomendationCollection: AngularFirestoreCollection<RecomendationRequest>;
      recomendations: Observable<RecomendationRequest[]>;
      // tslint:disable-next-line:no-inferrable-types
      collectionPath: string = 'articles';

    constructor(public fst: AngularFirestore, private storage: AngularFireStorage) {
      this.recomendationCollection = this.fst.collection(this.collectionPath);
      this.recomendations = this.recomendationCollection.snapshotChanges().pipe(map(changes => {
        return changes.map(a => {
          const data = a.payload.doc.data();
          data.FirebaseUID = a.payload.doc.id;
          return data;
        });
      }));
    }
/*
    addArticle(article: Article) {
     return this.articleCollection.add(article);
    }
*/
    getRecomendations() { // aca va el filtro
      console.log('ya estoy pidiendo cosas para recomendar');
      return this.recomendations.pipe(map(items => console.log(items)));
    }
/*
    deleteArticle(article: Article) {
     return this.fst.collection(this.collectionPath).doc(`${article.id}`).delete();
    }
*/
    updateRequest(rRequest: RecomendationRequest, recomendationAnswer: RecomendationAnswer) {
      return this.fst.collection(this.collectionPath).doc(`${rRequest.FirebaseUID}`).update({
        // toDo appendear la recomendacion.
      })
      .then(function() {
          console.log('Document successfully updated!');
      })
      .catch(function(error) {
          // The document probably doesn't exist.
          console.error('Error updating document: ', error);
      });
    }
  }
