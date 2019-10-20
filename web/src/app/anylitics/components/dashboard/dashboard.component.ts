import { Component, OnInit, OnDestroy } from '@angular/core';
import { StoreModel } from 'src/app/auth/models/store.model';
import { UserService } from 'src/app/auth/services/user.service';
import { AuthService } from 'src/app/auth/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AnyliticService } from '../../services/anylitics.service';
import { Interaction } from '../../model/interaction';
import { Visit } from '../../model/visit';
import { AnsweredRecom } from '../../model/answeredRecom';
import { PromotedArticle } from 'src/app/articles/models/promotedArticle';
import { DataService } from 'src/app/service/dataService';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  readyToRender = false;
  totalInteractions;
  positiveInteractions;
  negativeInteractions;
  sumPositiveInteractions;
  usersReached;
  usersClickedArticle;
  articlesSavedToCloset;
  popularTags;
  popularityOfTags;
  matrix;
  popularityXtag;
  bestTag;
  InteractionsXday;
  daysOfTheWeek = [];
  interactionsByDay = [];
  amountOfPromotedInteractions = 0;
  options: FormGroup;
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  interactions: Interaction[];
  visits: Visit[];
  subscriptions: Subscription[];
  subscriptors;
  feedBack: AnsweredRecom[];
  feedBackProm = 0;
  promotedArticles: PromotedArticle[];
  level1Articles = 0;
  level2Articles = 0;
  level3Articles = 0;
  tagsToRender: any;
  visitsByDay: any[];
  reactionsByDay: any[];
  favoriteByDay: any[];
  reactionsTooltip;
  tagsTooltip;
  graficosTooltip;
  recomendacionesTooltip: string;
  recomendacionesRatingTooltip: string;
  totalRecomendations: number;
  answeredRecomendations: number;

  constructor(
    fb: FormBuilder,
    public anyliticService: AnyliticService,
    public userService: UserService,
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) {
    this.options = fb.group({
      bottom: 0,
      fixed: false,
      top: 0
    });
    this.userStore.profilePh = '/assets/noProfilePic.png';
  }

  ngOnInit() {
    this.reactionsTooltip = `Indica la cantidad de prendas que le gustaron o no al usuario, al encontrarse con tus prendas.`;
    this.tagsTooltip = 'Indica la cantidad de reacciones positivas para tus 10 etiquetas más populares.';
    this.graficosTooltip =
    `Indica la cantidad de interacciones en las últimas dos semanas.
    \n • Interacciones Totales: Suma de visitas, reacciones (positivas y negativas) y prendas favoritas.
    \n • Visitas a prendas: Cantidad de veces que tu prenda se vió en detalle.
    \n • Prendas Favoritas: Cantidad de veces que tu prenda se guardó como favorita.
    \n • Reacciones: Cantidad de veces que un usuario, al ver tu prenda, reaccionó positiva o negativamente.`;

    this.recomendacionesTooltip = `Indica cuántas de tus recomendaciones fueron calificadas.
                                   \n Calificadas / Realizadas`;

    console.log('-+-+-+-+-+-Inicializando Estadisticas-+-+-+-+-+-');
    // this.route.data.subscribe(routeData => {
    //   const data = routeData['data'];
    //   if (data) {
    //     this.userStore = data;
    //   }
    // });
    this.dataService.getStoreInfo().then( store => {
      this.userStore = store;
      this.anyliticService.getInteractions(this.userStore.storeName).then((res) => this.interactions = res).then(() => {
        this.anyliticService.getVisits(this.userStore.storeName).then((res) => this.visits = res).then(() => {
          this.anyliticService.getSubscriptions(this.userStore.storeName).then((res) => this.subscriptions = res).then(() => {
            this.anyliticService.getRecomendationFeedBack(this.userStore.storeName).then((res) => this.feedBack = res).then(() => {
              this.anyliticService.getPromotedArticles(this.userStore.firebaseUID).then((res) => this.promotedArticles = res).then(() => {
                console.log('promotedArticles', this.promotedArticles);
                this.getAnylitics();
                setTimeout(() => {
                  this.readyToRender = true;
                }, 0);
              });
            });
          });
        });
      });
    });
  }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
  }

  logout() {
    this.authService.doLogout().then(
      res => {
        this.router.navigate(['/login']);
      },
      error => {
        console.log('Logout error', error);
      }
    );
  }

  getAnylitics() {
    console.log(1);
    // this.getTotalInteractions();
    this.getReactions();
    this.getUsersReached();
    // this.getUsersClickedArticle();
    // this.getArticlesSavedToCloset();
    this.getPopularsTags();
    // this.getBestTag();
    this.getInteractionsByDay();
    this.getSubcriptors();
    this.getPromOfFeedBack();
    // this.getAmountOfPromotedInteractions();
    this.sumPositiveInteractions = this.positiveInteractions + this.articlesSavedToCloset + this.usersClickedArticle;
    console.log(2)
  }

  getTotalInteractions() {
    this.totalInteractions = this.interactions.length;
    console.log(`tolalInteractions: ` + this.totalInteractions);
  }

  getReactions() {
    // tslint:disable-next-line: max-line-length
    const swipeInteractions = this.interactions.filter(interaction => interaction.clickOnArticle === false  && interaction.savedToCloset === false);
    this.positiveInteractions = swipeInteractions.filter(interaction => interaction.liked === true ).length;
    this.negativeInteractions = swipeInteractions.length - this.positiveInteractions;
  }

  divideAll() {
    this.level1Articles = this.level1Articles / 1;
    this.level2Articles = this.level2Articles / 1;
    this.level3Articles = this.level3Articles / 1;
  }

  getUsersReached() {
    this.usersReached = 0;
    const usersFound = [];
    this.interactions.forEach((interaction) => {
      if (!usersFound.includes(interaction.userId)) {
        this.usersReached++;
        usersFound.push(interaction.userId);
      }
    });
    console.log(`usersReached: ` + this.usersReached);
  }

  getUsersClickedArticle() {
    this.usersClickedArticle = this.interactions.filter(interaction => interaction.clickOnArticle === true).length;
    console.log(`clickedOnArticle: ` + this.usersClickedArticle);
  }

  getArticlesSavedToCloset() {
    this.articlesSavedToCloset = this.interactions.filter(interaction => interaction.savedToCloset === true).length;
    console.log(`savedToCloset: ` + this.articlesSavedToCloset);
  }

  getSubcriptors() {
    console.log(`subsss` + this.subscriptions);
    this.subscriptors = this.subscriptions.length;
  }

  getPopularsTags() {
    this.popularityXtag = [];
    this.popularTags = [];
    this.popularityOfTags = [];
    let index;
    this.interactions.forEach((interaction) => {
      if ((interaction.liked === true) && interaction.tags !== null) {
        interaction.tags.map((tag) => {
          if (!this.popularTags.includes(tag)) {
            this.popularTags.push(tag);
            this.popularityOfTags.push(0);
          }
          index = this.popularTags.indexOf(tag);
          this.popularityOfTags[index]++;
        });
      }
    });
    this.matrix = {};
    for (let i = 0; i < this.popularTags.length; i++) {
      this.matrix = {
        tag: this.popularTags[i],
        count: this.popularityOfTags[i]
      };
      this.popularityXtag.push(this.matrix);
    }

    const orderedArray = this.popularityXtag.sort((a, b) => (a.count > b.count) ? -1 : ((b.count > a.count) ? 1 : 0));
    console.log('orderedArray', orderedArray);

    this.tagsToRender = orderedArray.slice(0, 10);

    console.log(this.popularityXtag);
  }

  getBestTag() {
    let maxValue = -1;
    let indexOfMaxValue = -1;
    for (let i = 0; i < this.popularityOfTags.length; i++) {
      if (this.popularityXtag[i].count > maxValue) {
        indexOfMaxValue = i;
        maxValue = this.popularityXtag[i].count;
      }
    }
    if (this.popularityXtag[indexOfMaxValue]) {
      this.bestTag = this.popularityXtag[indexOfMaxValue].tag;
    } else {
      this.bestTag = 'No Disponible';
    }
  }


  getInteractionsByDay() {
    this.InteractionsXday = [];
    this.daysOfTheWeek = [];
    this.interactionsByDay = [];
    this.visitsByDay = [];
    this.reactionsByDay = [];
    this.favoriteByDay = [];
    let index;
    const today = new Date();
    this.interactions.forEach((interaction) => {
      if (interaction.interactionTime !== undefined) {
        const dateOfInteraction: Date = new Date(interaction.interactionTime.toDate());
        if (!this.daysOfTheWeek.includes(`${dateOfInteraction.getDate()}/${dateOfInteraction.getMonth() + 1}`)) {
          this.daysOfTheWeek.push(`${dateOfInteraction.getDate()}/${dateOfInteraction.getMonth() + 1}`);
          this.interactionsByDay.push(0);
          this.visitsByDay.push(0);
          this.reactionsByDay.push(0);
          this.favoriteByDay.push(0);
        }
        index = this.daysOfTheWeek.indexOf(`${dateOfInteraction.getDate()}/${dateOfInteraction.getMonth() + 1}`);
        this.interactionsByDay[index]++;
        if (interaction.clickOnArticle === true) { this.visitsByDay[index]++; }
        if (interaction.clickOnArticle === false && interaction.savedToCloset === false ) { this.reactionsByDay[index]++; }
        if (interaction.savedToCloset === true ) { this.favoriteByDay[index]++; }
      }
    });

    this.matrix = {};
    for (let i = 0; i < this.daysOfTheWeek.length; i++) {
      this.matrix = {
        day: this.daysOfTheWeek[i],
        count: this.interactionsByDay[i]
      };
      this.InteractionsXday.push(this.matrix);
    }

    console.log(this.InteractionsXday);
  }

  getPromOfFeedBack() {
    this.totalRecomendations = this.feedBack.length;
    this.answeredRecomendations = 0;
    console.log('haciendo el calculo de las estrellas', this.feedBack);
    const filteredFeedBack = this.feedBack.filter(feedBack => {
      return feedBack.feedBack;
    });
    this.answeredRecomendations = filteredFeedBack.length
    let sum = 0;
    for (let i = 0; i < filteredFeedBack.length; i++) {
      // tslint:disable-next-line: radix
      sum = sum + parseInt(filteredFeedBack[i].feedBack);
      console.log(`sumatoria parcial ` + sum);
    }
    this.feedBackProm = (sum / filteredFeedBack.length);
    console.log(`feedBackProm ` + this.feedBackProm);
    this.recomendacionesRatingTooltip = `Promedio de los puntajes asignados a las recomendaciones calificadas.
    \n• Valor exacto: ${this.feedBackProm.toFixed(2)}`;
  }

  getAmountOfPromotedInteractions() {
    for (let i = 0; i < this.interactions.length; i++) {
      switch (this.getLevelOfPromotion(this.interactions[i])) {
        case 1: {
           // statements;
           this.level1Articles++;
           break;
        }
        case 2: {
           // statements;
           this.level2Articles++;
           break;
        }
        default: {
           // statements;
           this.level3Articles++;
           break;
        }
     }
    }
    this.divideAll();
  }

  getLevelOfPromotion(interaction: Interaction) {
    for (let i = 0; i < this.promotedArticles.length; i++) {
      if (interaction.articleId === this.promotedArticles[i].articleId) {
        const dateOfInteraction: Date = new Date(interaction.interactionTime.toDate());
        const dateStartOfPromotion: Date = new Date(this.promotedArticles[i].startOfPromotion.toDate());
        const dateEndPromotion: Date = new Date(this.promotedArticles[i].endOfPromotion.toDate());
        if ( this.compareDate( dateOfInteraction, dateStartOfPromotion ) === 1 &&
         this.compareDate( dateOfInteraction, dateEndPromotion ) === -1 ) {
           return this.promotedArticles[i].promotionLevel;
         } else {
           return 1;
         }
      }
    }
    return 1;
  }

  compareDate(date1: Date, date2: Date): number {
    // With Date object we can compare dates them using the >, <, <= or >=.
    // The ==, !=, ===, and !== operators require to use date.getTime(),
    // so we need to create a new instance of Date with 'new Date()'
    const d1 = date1; const d2 = date2;

    // Check if the dates are equal
    const same = d1.getTime() === d2.getTime();
    if (same) {return 0 ; }

    // Check if the first is greater than second
    if (d1.getTime() > d2.getTime()) {return 1; }

    // Check if the first is less than second
    if (d1.getTime() < d2.getTime()) { return -1; }
  }

  goToProfile() {
    console.log(`/store/${this.userStore.storeName}`);
    this.router.navigate([`/home`]);
    console.log(this.userStore.profilePh);
  }

  goToInventory() {
    this.router.navigate([`/inventory`]);
  }

  goToRecomendations() {
    this.router.navigate([`/recomendations`]);
  }

  goToAnalytics() {
    console.log(`already in analytics`);
  }

}


