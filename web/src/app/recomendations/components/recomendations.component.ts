import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService } from '../../auth/services/user.service';
import { AuthService } from '../../auth/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { StoreModel } from '../../auth/models/store.model';
import { Subscription } from 'rxjs';
import { RecomendationRequest } from '../model/recomendationRequest.model';
import { timestamp } from 'rxjs/operators';
import { toDate } from '@angular/common/src/i18n/format_date';
import { Article } from '../../articles/models/article';
import { ArticleService } from '../../articles/services/article.service';
import { MatTableDataSource, MatSnackBar } from '@angular/material';
import { NgxSpinnerService } from 'ngx-spinner';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-recomendations',
  templateUrl: './recomendations.component.html',
  styleUrls: ['./recomendations.component.css']
})
export class RecomendationsComponent implements OnInit, OnDestroy {
  firebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[];
  _subscription2: Subscription;
  _subscription: Subscription;
  selectedRequest: RecomendationRequest = new RecomendationRequest();
  selectedArticle: Article = new Article();
  selectedRowIndex = -1;
  requestAnswerForm: FormGroup;
  answeredRequestIndex = -1;
  selectedArticleRowIndex: any;
  constructor(
    public snackBar: MatSnackBar,
    public articleService: ArticleService,
    public userService: UserService,
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService,
    private fb: FormBuilder,
    ) { this.createForm();
        console.log(`vamps con este nombre ` + this.userStore.storeName);
      }

      dataSourceRequests: RecomendationRequest[] = [
         {FirebaseUID: '1' ,
          UserName: 'ADonato', Description: 'Lorem ipsum dolor sit amet consectetur adipiscing elit enim auctor',
          // tslint:disable-next-line:max-line-length
          ArticleUrl: 'https://firebasestorage.googleapis.com/v0/b/mylook-develop.appspot.com/o/test%2F1536083661870_camisa%20roja%20case.jpeg?alt=media&token=e3244600-38ef-4c7c-bf73-a254d51db290',
          Localization: [60],
          Answers: []},
          {FirebaseUID: '2' ,
          UserName: 'ADonato', Description: 'Lorem ipsum dolor sit amet consectetur adipiscing elit enim auctor',
          // tslint:disable-next-line:max-line-length
          ArticleUrl: 'https://firebasestorage.googleapis.com/v0/b/mylook-develop.appspot.com/o/test%2F1536083661870_camisa%20roja%20case.jpeg?alt=media&token=e3244600-38ef-4c7c-bf73-a254d51db290',
          Localization: [60],
          Answers: []},
          {FirebaseUID: '3' ,
          UserName: 'AeeeDonato', Description: 'Lorem ipsum dolor sit amet consectetur adipiscing elit enim auctor',
          // tslint:disable-next-line:max-line-length
          ArticleUrl: 'https://firebasestorage.googleapis.com/v0/b/mylook-develop.appspot.com/o/test%2F1536084641338_Remera%20lisa%20azul.jpg?alt=media&token=bcc00b29-96be-4ac3-b900-1b07e880346b',
          Localization: [60],
          Answers: []},
    ] ;
    displayedColumnsRequests: string[] = [
      'ListaPeticiones',
    ];
    dataSourceArticles;
    displayedColumnsArticles: string[] = [
      'PrendasCatalogo'
    ];

  ngOnInit() {
    this.spinner.show();
    this.route.data.subscribe(routeData => {
      console.log('estoy ya en el inventario');
      const data = routeData['data'];
          if (data) {
            this.firebaseUser = data;
        }
      });
      this._subscription = this.userService.getUserInfo(this.firebaseUser.firebaseUserId).subscribe(userA => {
        this.userStore = userA[0];
        if ( this.userStore.profilePh === undefined) {this.userStore.profilePh = this.firebaseUser.profilePh; }
        this._subscription2 = this.articleService.getArticles(this.userStore.storeName).subscribe(articles => {
          this.articles = articles;
          this.dataSourceArticles = new MatTableDataSource(this.articles);
          setTimeout(() => {
            /** spinner ends after  seconds */
            this.spinner.hide();
          }, 2000);
        });
      });
      }

  ngOnDestroy() {
    console.log('destruyendo subscripciones');
    this._subscription.unsubscribe();
    this._subscription2.unsubscribe();
  }

  createForm() {
    this.requestAnswerForm = this.fb.group({
      storeName: ['', Validators.nullValidator], // verificar que se envie
      article: ['', Validators.nullValidator],
      description: ['', Validators.nullValidator],
      isFavorite: ['', Validators.nullValidator],
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
    console.log(row);
    this.selectedRowIndex = row.FirebaseUID;
    console.log(`selectedRowIndex request ` + this.selectedRowIndex);
    this.selectedRequest = row;
    this.selectedArticle = new Article();
    this.selectedArticle.picture = '/assets/idea.png';
    console.log(this.userStore.profilePh);
  }

  showInformationArticle(row) {
    console.log(row);
    this.selectedArticleRowIndex = row.id;
    console.log(`selectedRowIndex article ` + this.selectedRowIndex);
    this.selectedArticle = row;
  }

  sendAnswer(row) {
    this.answeredRequestIndex = this.selectedRowIndex;
    this.openSnackBar('Se ha enviado la sugerencia!' , 'cerrar');

  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }


}
