import {
  AngularFirestore,
  AngularFirestoreCollection
} from 'angularfire2/firestore';
import { AngularFireStorage } from 'angularfire2/storage';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { RecomendationRequest } from '../model/recomendationRequest.model';
import { RecomendationAnswer } from '../model/recomendationAnswer.model';
import * as firebase from 'firebase';

@Injectable()
export class RecomendationService {
  recomendationRequestCollection: AngularFirestoreCollection<RecomendationRequest>;
  recomendationAnswerCollection: AngularFirestoreCollection<RecomendationAnswer>;
  recomendations: Observable<RecomendationRequest[]>;
  requestCollectionPath = 'requestRecommendations';
  answerCollectionPath = 'answeredRecommendations';
  categories: any[];
  db: firebase.firestore.Firestore;
  categoriesPath = 'categories';
  sexes: any[];

  constructor(public fst: AngularFirestore, private storage: AngularFireStorage) {
    this.db = firebase.firestore();
    this.recomendationAnswerCollection = this.fst.collection(this.answerCollectionPath);
    this.recomendationRequestCollection = this.fst.collection(this.requestCollectionPath);
    this.recomendations = this.recomendationRequestCollection.snapshotChanges().pipe(map(changes => {
      return changes.map(a => {
        const data = a.payload.doc.data();
        data.FirebaseUID = a.payload.doc.id;
        if (a.payload.doc.data().requestPhoto === '') {
          data.requestPhoto = '/assets/brandNoBackground.png';
        }
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
    return this.recomendations; // devolvemos todo
  }

  getCategories() {
    this.categories = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos trayendo categorias`);
      const res = this.db.collection(this.categoriesPath).where('name', '==', 'recommendation')
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            data.id = doc.id;
            resolve(data.categories);
            console.log(data.categories);
          });
        }).then(() => {
          resolve(this.categories);
        })
        .catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  getSexes() {
    this.sexes = [];
    return new Promise<any>((resolve, reject) => {
      console.log(`estamos trayendo sexes`);
      const res = this.db.collection(this.categoriesPath).where('name', '==', 'sexo')
        .get().then((querySnapshot) => {
          querySnapshot.forEach((doc) => {
            const data = doc.data();
            resolve(data.categories);
          });
        }).then(() => {
          resolve(this.sexes);
        })
        .catch(function (error) {
          console.log('Error getting documents: ', error);
          reject(error);
        });
    });
  }

  updateRequest(rRequest: RecomendationRequest, recomendationAnswer: RecomendationAnswer) {
    return this.fst.collection(this.requestCollectionPath).doc(`${rRequest.FirebaseUID}`).update({
      // toDo appendear la recomendacion.
    })
      .then(function () {
        console.log('Document successfully updated!');
      })
      .catch(function (error) {
        // The document probably doesn't exist.
        console.error('Error updating document: ', error);
      });
  }

  addRecomendationAnswer(answer, requestUID) {
    const requestRef = this.fst.collection(this.requestCollectionPath).doc(requestUID);
    return requestRef.update({
      answers: firebase.firestore.FieldValue.arrayUnion(answer)
    });
  }

  storeAnswer(answer) {
    return this.recomendationAnswerCollection.add(answer);
  }
}


