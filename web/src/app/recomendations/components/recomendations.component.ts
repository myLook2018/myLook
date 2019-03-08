import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService } from '../../auth/services/user.service';
import { AuthService } from '../../auth/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { StoreModel } from '../../auth/models/store.model';
import { Subscription } from 'rxjs';
import { RecomendationRequest } from '../model/recomendationRequest.model';
import { Article } from '../../articles/models/article';
import { ArticleService } from '../../articles/services/article.service';
import { MatTableDataSource, MatSnackBar } from '@angular/material';
import { NgxSpinnerService } from 'ngx-spinner';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormControl
} from '@angular/forms';
import { RecomendationService } from '../service/recomendationService';
import { RecomendationAnswer } from '../model/recomendationAnswer.model';

@Component({
  selector: 'app-recomendations',
  templateUrl: './recomendations.component.html',
  styleUrls: ['./recomendations.component.scss']
})
export class RecomendationsComponent implements OnInit, OnDestroy {
  over: any;
  logout:any;
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
  userSubscription: Subscription;
  articleSubscription: Subscription;
  recomendationSubscription: Subscription;
  selectedRequest: RecomendationRequest = new RecomendationRequest();
  selectedArticle: Article = new Article();
  selectedRowIndex = -1;
  requestAnswerForm: FormGroup;
  answerForm: FormGroup;
  answeredRequestIndex = -1;
  selectedArticleRowIndex: -1;
  finishedLoading = false;
  selectedAnswer: RecomendationAnswer;
  description = '';
  isRequestSelected = false;
  constructor(
    public snackBar: MatSnackBar,
    public articleService: ArticleService,
    public userService: UserService,
    public authService: AuthService,
    public recomendationsService: RecomendationService,
    private router: Router,
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService,
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
    this.spinner.show();
    this.route.data.subscribe(routeData => {
      console.log(`trayendo recomendaciones`);
      const data = routeData['data'];
      if (data) {
        this.firebaseUser = data;
      }
    });
    console.log(1);
    this.userSubscription = this.userService
      .getUserInfo(this.firebaseUser.firebaseUserId)
      .subscribe(userA => {
        console.log(2);
        this.userStore = userA[0];
        if (this.userStore.profilePh === undefined) {
          console.log(3);
          this.userStore.profilePh = this.firebaseUser.profilePh;
        }
        console.log(4);
        this.articleService.getArticlesCopado(this.userStore.storeName).then((articles) => {
          this.dataSourceArticles = [];
          console.log(articles);
          this.articles = articles;
          this.dataSourceArticles = new MatTableDataSource(this.articles);
          console.log(articles);
          this.recomendationsService.getCategories().then((categos) => {
            console.log(categos);
            this.categories = categos;
          });
          this.recomendationsService.getSexes().then((sexs) => {
            console.log(sexs);
            this.sexes = sexs;
          });
          this.recomendationSubscription = this.recomendationsService.getRecomendations()
            .subscribe(recomendations => {
              console.log(recomendations);
              console.log(6);
              this.recomendationsRequests = recomendations;
              this.determineRequestToAnswer();
              this.dataSourceRequests = new MatTableDataSource(
                this.recomendationsToAnswer
              );
              console.log(7);
              this.dataSourceAnswered = new MatTableDataSource(
                this.recomendationsAnswered
              );
              console.log(8);
            });
          setTimeout(() => {
            console.log(9);
            /** spinner ends after  seconds */
            this.spinner.hide();
            console.log(`este es el row index ` + this.selectedArticleRowIndex);
          }, 2000);
        });
      });
  }

  ngOnDestroy() {
    console.log('destruyendo subscripciones');
    this.userSubscription.unsubscribe();
    this.recomendationSubscription.unsubscribe();
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
    this.requestAnswerForm.get('description').setValue('');
    this.isRequestSelected = true;
    this.selectedRowIndex = row.FirebaseUID;
    this.selectedRequest = row;
    this.selectedArticle = new Article();
    this.selectedArticle.picture = '/assets/idea.png';
    console.log(this.userStore.profilePh);
  }

  showInformationAnswer(row) {
    this.selectedArticle = row;
    this.selectedArticleRowIndex = -1;
    this.isRequestSelected = false;
    this.selectedRowIndex = row.FirebaseUID;
    this.selectedRequest = row;
    this.selectedAnswer = this.selectedRequest.answers.find(
      answer => answer.storeName === this.userStore.storeName
    );
    this.selectedArticle.picture = this.selectedAnswer.articlePhoto;
    this.requestAnswerForm
      .get('description')
      .setValue(this.selectedAnswer.description);
  }

  showInformationArticle(row, index) {
    if (this.isRequestSelected) {
      this.selectedArticleRowIndex = row.articleId;
      this.selectedArticle = row;
    }
  }

  sendAnswer() {
    this.requestAnswerForm.get('articleUID').setValue(this.selectedArticle.articleId);
    if (this.requestAnswerForm.get('articleUID').value === undefined) {
      this.error = 'Se requiere que selecione una prenda de su catalogo para recomendar';
      console.log(this.error);
      return this.error;
    } else {
      this.answeredRequestIndex = this.selectedRowIndex;
      this.requestAnswerForm.get('storeName').setValue(this.userStore.storeName);
      this.requestAnswerForm.get('articlePhoto').setValue(this.selectedArticle.picture);
      this.requestAnswerForm.get('storePhoto').setValue(this.userStore.profilePh);
      this.requestAnswerForm.get('feedBack').setValue('');
      console.log(3);
      this.recomendationsService.addRecomendationAnswer(this.requestAnswerForm.value, this.selectedRequest.FirebaseUID)
        .then(() => {
          console.log(4);
          this.answerForm.get('requestUID').setValue(this.selectedRequest.FirebaseUID);
          this.answerForm.get('storeName').setValue(this.userStore.storeName);
          console.log(5);
          this.recomendationsService.storeAnswer(this.answerForm.value)
            .then(() => {
              console.log(6);
              this.openSnackBar('Se ha enviado la sugerencia!', 'cerrar');
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
      console.log(`filtrando ambos: ${this.selectedCatego} y ${this.selectedSex} `);
      this.newRecos = this.recomendationsToAnswer.filter(x => x.category === this.selectedCatego && x.sex === this.selectedSex);
      console.log(this.newRecos);
    } else if (this.selectedCatego !== undefined) {
      console.log(`filtrando catego ${this.selectedCatego}`);
      this.newRecos = this.recomendationsToAnswer.filter(x => x.category === this.selectedCatego);
      console.log(this.newRecos);
    } else if (this.selectedSex !== undefined) {
      console.log(`filtrando sex ${this.selectedSex}`);
      this.newRecos = this.recomendationsToAnswer.filter(x => x.sex === this.selectedSex);
      console.log(this.newRecos);
    } else {
      console.log(`filtros reseteados, traigo todo`);
      this.newRecos = this.recomendationsToAnswer;
      console.log(this.newRecos);
    }
    this.dataSourceRequests = new MatTableDataSource(
      this.newRecos
    );

  }

  goToProfile() {
    console.log(`/home`);
    this.router.navigate([`/home`]);
    console.log(this.userStore.profilePh);
  }

  goToInventory() {
    this.router.navigate([`/inventory`]);
  }

  goToRecomendations() {
    console.log(`already in recomendations`);
  }

  goToAnalytics() {
    this.router.navigate([`/analytics`]);
  }

}
