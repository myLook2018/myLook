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
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { RecomendationService } from '../service/recomendationService';
import { RecomendationAnswer } from '../model/recomendationAnswer.model';

@Component({
  selector: 'app-recomendations',
  templateUrl: './recomendations.component.html',
  styleUrls: ['./recomendations.component.scss']
})
export class RecomendationsComponent implements OnInit, OnDestroy {
  firebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[];
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
  selectedArticleRowIndex: any;
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
    private fb: FormBuilder,
    ) { this.createForm();
        console.log(`vamps con este nombre ` + this.userStore.storeName);
      }

      dataSourceRequests;
      displayedColumnsRequests: string[] = [
        'ListaPeticiones',
      ];
      dataSourceAnswered;
      displayedColumnsAnswered: string[] = [
        'ListaPeticiones',
      ];
      dataSourceArticles;
    displayedColumnsArticles: string[] = [
      'PrendasCatalogo'
    ];

  ngOnInit() {
    this.spinner.show();
    this.route.data.subscribe(routeData => {
      console.log(`trayendo recomendaciones`);
      const data = routeData['data'];
          if (data) {
            this.firebaseUser = data;
        }
      });
      this.userSubscription = this.userService.getUserInfo(this.firebaseUser.firebaseUserId).subscribe(userA => {
        this.userStore = userA[0];
        if ( this.userStore.profilePh === undefined) {this.userStore.profilePh = this.firebaseUser.profilePh; }
        this.articleSubscription = this.articleService.getArticles(this.userStore.storeName).subscribe(articles => {
          this.articles = articles;
          this.dataSourceArticles = new MatTableDataSource(this.articles);
          this.recomendationSubscription = this.recomendationsService.getRecomendations().subscribe( recomendations => {
            this.recomendationsRequests = recomendations;
            this.determineRequestToAnswer();
            this.dataSourceRequests = new MatTableDataSource(this.recomendationsToAnswer);
            this.dataSourceAnswered = new MatTableDataSource(this.recomendationsAnswered);
          });
          setTimeout(() => {
            /** spinner ends after  seconds */
            this.spinner.hide();
          }, 2000);
        });
      });
      }

  ngOnDestroy() {
    console.log('destruyendo subscripciones');
    this.userSubscription.unsubscribe();
    this.articleSubscription.unsubscribe();
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
      feedBack: ['', Validators.nullValidator],
    });
    this.answerForm = this.fb.group({
      requestUID: ['', Validators.nullValidator],
      storeName: ['', Validators.nullValidator],
    });
  }

  goToProfile() {
    console.log(`/store/${this.userStore.storeName}`);
    this.router.navigate([`/store/${this.userStore.storeName}`]);
    console.log(this.userStore.profilePh);
  }

  goToInventory() {
    this.router.navigate([`/home`]);
  }

  showInformationRequest(row) {
    this.isRequestSelected = true;
    this.selectedRowIndex = row.FirebaseUID;
    this.selectedRequest = row;
    this.selectedArticle = new Article();
    this.selectedArticle.picture = '/assets/idea.png';
    console.log(this.userStore.profilePh);
  }

  showInformationAnswer(row) {
    this.selectedArticle = row;
    this.selectedArticleRowIndex = undefined;
    this.isRequestSelected = false;
    this.selectedRowIndex = row.FirebaseUID;
    this.selectedRequest = row;
    this.selectedAnswer = this.selectedRequest.answers.find(answer => answer.storeName === this.userStore.storeName);
    this.selectedArticle.picture = this.selectedAnswer.articlePhoto;
    this.requestAnswerForm.get('description').setValue(this.selectedAnswer.description);
  }

  showInformationArticle(row) {
    if (this.isRequestSelected) {
      this.selectedArticleRowIndex = row.id;
      this.selectedArticle = row;
    }
  }

  sendAnswer() {
    this.answeredRequestIndex = this.selectedRowIndex;
    this.requestAnswerForm.get('storeName').setValue(this.userStore.storeName);
    this.requestAnswerForm.get('articlePhoto').setValue(this.selectedArticle.picture);
    this.requestAnswerForm.get('articleUID').setValue(this.selectedArticle.id);
    this.requestAnswerForm.get('storePhoto').setValue(this.userStore.profilePh);
    this.requestAnswerForm.get('feedBack').setValue('');
    this.recomendationsService.addRecomendationAnswer(this.requestAnswerForm.value, this.selectedRequest.FirebaseUID).then(() => {
      this.answerForm.get('requestUID').setValue(this.selectedRequest.FirebaseUID);
      this.answerForm.get('storeName').setValue(this.userStore.storeName);
      this.recomendationsService.storeAnswer(this.answerForm.value).then(() => {
        this.openSnackBar('Se ha enviado la sugerencia!' , 'cerrar');
      });
    });

  }

  determineRequestToAnswer() {
    this.recomendationsToAnswer = [];
    this.recomendationsAnswered = [];
    this.recomendationsRequests.map(request => {
      if (this.isInAnswers(request.answers)) {
        this.recomendationsAnswered.push(request);
      } else {
        this.recomendationsToAnswer.push(request); }
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
      duration: 2000,
    });
  }

  loadingSpinner() {
    setTimeout(() => {
      /** spinner ends after  seconds */
      this.finishedLoading = true;
    }, 2000);
  }

}
