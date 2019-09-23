import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { StoreModel } from '../../auth/models/store.model';
import { Subscription } from 'rxjs';
import { RecomendationRequest } from '../model/recomendationRequest.model';
import { Article } from '../../articles/models/article';
import { ArticleService } from '../../articles/services/article.service';
import { MatTableDataSource, MatSnackBar } from '@angular/material';
import {
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { RecomendationService } from '../service/recomendationService';
import { RecomendationAnswer } from '../model/recomendationAnswer.model';
import { DataService } from 'src/app/service/dataService';

@Component({
  selector: 'app-recomendations',
  templateUrl: './recomendations.component.html',
  styleUrls: ['./recomendations.component.scss']
})
export class RecomendationsComponent implements OnInit, OnDestroy {
  over: any;
  logout: any;
  selectedCatego = undefined;
  selectedSex = undefined;
  newRecos: RecomendationRequest[] = [];
  error: any;
  firebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[];
  categories = [];
  sexes = [];
  recomendationsRequests: RecomendationRequest[];
  recomendationsAnswered: RecomendationRequest[] = [];
  recomendationsToAnswer: RecomendationRequest[] = [];
  articleSubscription: Subscription;
  recomendationSubscription: Subscription;
  selectedRequest: RecomendationRequest = new RecomendationRequest();
  selectedArticle: Article;
  selectedRowIndex = -1;
  requestAnswerForm: FormGroup;
  answerForm: FormGroup;
  answeredRequestIndex = -1;
  selectedArticleRowIndex: -1;
  finishedLoading = false;
  selectedAnswer: RecomendationAnswer;
  description = '';
  isRequestSelected = false;
  disableSendRecomendation = false;
  isAnAnswer: boolean;

  constructor(
    public snackBar: MatSnackBar,
    public articleService: ArticleService,
    public dataService: DataService,
    public recomendationsService: RecomendationService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.createForm();
    console.log(`vamps con este nombre ` + this.userStore.storeName);
  }

  dataSourceRequests;
  displayedColumnsRequests: string[] = ['ListaPeticiones'];
  dataSourceAnswered;
  displayedColumnsAnswered: string[] = ['ListaPeticiones'];
  dataSourceArticles;
  displayedColumnsArticles: string[] = ['PrendasCatalogo'];

  ngOnInit() {
    this.selectedArticle = new Article();
    console.log('-+-+-+-+-+-Inicializando Recomendaciones-+-+-+-+-+-');
    console.log('-+-+-+-+-+-Inicializando Inventario-+-+-+-+-+-');
    this.dataService.getStoreInfo().then(store => {
      this.userStore = store;
      this.articleService
        .getArticlesCopado(this.userStore.storeName)
        .then(articles => {
          this.dataSourceArticles = [];
          console.log(articles);
          this.articles = articles;
          this.dataSourceArticles = new MatTableDataSource(this.articles);
          console.log(articles);
          this.recomendationsService.getCategories().then(categos => {
            console.log(categos);
            this.categories = categos;
          });
          this.recomendationsService.getSexes().then(sexs => {
            console.log(sexs);
            this.sexes = sexs;
          });
          this.recomendationSubscription = this.recomendationsService
            .getRecomendations()
            .subscribe(recomendations => {
              console.log(recomendations);
              console.log(1);
              this.recomendationsRequests = recomendations;
              this.determineRequestToAnswer();
              this.dataSourceRequests = new MatTableDataSource(
                this.recomendationsToAnswer
              );
              console.log(2);
              this.dataSourceAnswered = new MatTableDataSource(
                this.recomendationsAnswered
              );
              console.log(3);
            });
        });
    });
  }

  ngOnDestroy() {
    console.log('destruyendo subscripciones');
    if (this.recomendationSubscription) {
      this.recomendationSubscription.unsubscribe();
    }
  }

  createForm() {
    this.requestAnswerForm = this.fb.group({
      storeName: ['', Validators.nullValidator], // verificar que se envie
      // requestUID: ['', Validators.nullValidator],
      articlePhoto: ['', Validators.nullValidator],
      articleUID: ['', Validators.nullValidator],
      description: ['', Validators.nullValidator],
      storePhoto: ['', Validators.nullValidator],
      feedBack: ['', Validators.nullValidator]
    });
    this.answerForm = this.fb.group({
      requestUID: ['', Validators.nullValidator],
      storeName: ['', Validators.nullValidator]
    });
  }

  showInformationRequest(row) {
    this.disableSendRecomendation = false;
    this.requestAnswerForm.get('description').setValue('');
    this.isRequestSelected = true;
    this.selectedRowIndex = row.FirebaseUID;
    this.selectedRequest = row;
    if (!this.selectedRequest.requestPhoto) {
      this.selectedRequest.requestPhoto =
      // tslint:disable-next-line: max-line-length
      'https://firebasestorage.googleapis.com/v0/b/mylook-develop.appspot.com/o/utils%2Flogo_transparente_50.png?alt=media&token=c72e5b39-3011-4f26-ba4f-4c9f7326c68a';
    }
    this.selectedArticle = new Article();
    this.selectedArticle.picturesArray[0] = '/assets/idea.png';
    console.log(row);
  }

  showInformationAnswer(row, isAlreadyRecomended = false) {
    // this.isAnAnswer = isAlreadyRecomended;
    console.log('row', row);
    console.log('selectedArticle', this.selectedArticle);
    this.selectedArticle = new Article();
    // this.selectedArticle = row;
    // console.log('selectedArticle', this.selectedArticle);
    this.selectedArticleRowIndex = -1;
    this.isRequestSelected = false;
    this.selectedRowIndex = row.FirebaseUID;
    this.selectedRequest = row;
    this.selectedAnswer = this.selectedRequest.answers.find(
      answer => answer.storeName === this.userStore.storeName
      );
      this.selectedArticle.picturesArray[0] = this.selectedAnswer.articlePhoto;
      this.requestAnswerForm
      .get('description')
      .setValue(this.selectedAnswer.description);
      this.isRequestSelected = true;
      this.disableSendRecomendation = true;
  }

  showInformationArticle(row, index) {
    if (this.isRequestSelected) {
      this.selectedArticleRowIndex = row.articleId;
      this.selectedArticle = row;
    }
  }

  sendAnswer() {
    this.requestAnswerForm
      .get('articleUID')
      .setValue(this.selectedArticle.articleId);
    if (this.requestAnswerForm.get('articleUID').value === undefined) {
      this.error =
        'Se requiere que selecione una prenda de su catalogo para recomendar';
      console.log(this.error);
      this.openSnackBar(this.error, 'x');
      return this.error;
    } else {
      this.disableSendRecomendation = true;
      this.answeredRequestIndex = this.selectedRowIndex;
      this.requestAnswerForm
        .get('storeName')
        .setValue(this.userStore.storeName);
      this.requestAnswerForm
        .get('articlePhoto')
        .setValue(this.selectedArticle.picturesArray[0]);
      this.requestAnswerForm
        .get('storePhoto')
        .setValue(this.userStore.profilePh);
      this.requestAnswerForm.get('feedBack').setValue('');
      console.log(3);
      this.recomendationsService
        .addRecomendationAnswer(
          this.requestAnswerForm.value,
          this.selectedRequest.FirebaseUID
        )
        .then(() => {
          console.log(4);
          this.answerForm
            .get('requestUID')
            .setValue(this.selectedRequest.FirebaseUID);
          this.answerForm.get('storeName').setValue(this.userStore.storeName);
          console.log(5);
          this.recomendationsService
            .storeAnswer(this.answerForm.value)
            .then(() => {
              console.log(6);
              this.openSnackBar('Se ha enviado la sugerencia!', 'x');
              if(this.recomendationsToAnswer.length > 0) {
                this.showInformationRequest(this.recomendationsToAnswer[0]);
              }
            });
        });
    }
  }

  determineRequestToAnswer() {
    this.recomendationsToAnswer = [];
    this.recomendationsAnswered = [];
    this.recomendationsRequests.map(request => {
      if (request.isClosed === false) {
        if (this.isInAnswers(request.answers)) {
          this.recomendationsAnswered.push(request);
        } else {
          this.recomendationsToAnswer.push(request);
        }
      }
    });
  }

  isInAnswers(answers: RecomendationAnswer[]) {
    let res = false;
    answers.forEach(answer => {
      if (answer.storeName === this.userStore.storeName) {
        res = true;
      }
    });
    return res;
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000
    });
  }

  loadingSpinner() {
    setTimeout(() => {
      /** spinner ends after  seconds */
      this.finishedLoading = true;
    }, 2000);
  }

  filterRecos() {
    this.newRecos = [];
    if (this.selectedCatego !== undefined && this.selectedSex !== undefined) {
      console.log(
        `filtrando ambos: ${this.selectedCatego} y ${this.selectedSex} `
      );
      this.newRecos = this.recomendationsToAnswer.filter(
        x => x.category === this.selectedCatego && x.sex === this.selectedSex
      );
      console.log(this.newRecos);
    } else if (this.selectedCatego !== undefined) {
      console.log(`filtrando catego ${this.selectedCatego}`);
      this.newRecos = this.recomendationsToAnswer.filter(
        x => x.category === this.selectedCatego
      );
      console.log(this.newRecos);
    } else if (this.selectedSex !== undefined) {
      console.log(`filtrando sex ${this.selectedSex}`);
      this.newRecos = this.recomendationsToAnswer.filter(
        x => x.sex === this.selectedSex
      );
      console.log(this.newRecos);
    } else {
      console.log(`filtros reseteados, traigo todo`);
      this.newRecos = this.recomendationsToAnswer;
      console.log(this.newRecos);
    }
    console.log('this.newRecos', this.newRecos)
    this.recomendationsRequests = this.newRecos;
    this.dataSourceRequests = new MatTableDataSource(this.recomendationsRequests);
  }

  goToProfile() {
    console.log(`/home`);
    this.router.navigate([`/home`]);
    console.log(this.userStore.profilePh);
  }

  goToInventory() {
    this.router.navigate([`/catalogo`]);
  }

  goToRecomendations() {
    console.log(`already in recomendations`);
  }

  goToAnalytics() {
    this.router.navigate([`/analytics`]);
  }
}
