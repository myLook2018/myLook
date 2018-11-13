import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { StoreModel } from 'src/app/auth/models/store.model';
import { UserService } from 'src/app/auth/services/user.service';
import { AuthService } from 'src/app/auth/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { AnyliticService } from '../../services/anylitics.service';
import { Interaction } from '../../model/interaction';
import { Visit } from '../../model/visit';
import { AnsweredRecom } from '../../model/answeredRecom';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  readyToRender = false;
  totalInteractions;
  positiveInteractions;
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
  _subscription: Subscription;
  interactions: Interaction[];
  visits: Visit[];
  subscriptions: Subscription[];
  subscriptors;
  feedBack: AnsweredRecom[];
  feedBackProm = 0;
  val1 = 2;

  constructor(
    fb: FormBuilder,
    public anyliticService: AnyliticService,
    public userService: UserService,
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService, ) {
    this.options = fb.group({
      bottom: 0,
      fixed: false,
      top: 0
    });
    this.userStore.profilePh = '/assets/noProfilePic.png';
  }

  ngOnInit() {
    this.spinner.show();
    this.route.data.subscribe(routeData => {
      const data = routeData['data'];
      if (data) {
        this.FirebaseUser = data;
      }
    });
    this._subscription = this.userService.getUserInfo(this.FirebaseUser.firebaseUserId).subscribe(userA => {
      this.userStore = userA[0];
      if (this.userStore.profilePh === '') { this.userStore.profilePh = this.FirebaseUser.profilePh; }
      this.anyliticService.getInteractions(this.userStore.storeName).then((res) => this.interactions = res).then(() => {
        this.anyliticService.getVisits(this.userStore.storeName).then((res) => this.visits = res).then( () => {
          this.anyliticService.getSubscriptions(this.userStore.storeName).then((res) => this.subscriptions = res).then(() => {
            this.anyliticService.getRecomendationFeedBack(this.userStore.storeName).then((res) => this.feedBack = res).then( () => {
              this.getAnylitics();
              setTimeout(() => {
                this.spinner.hide();
                this.readyToRender = true;
              }, 2000);
            });
          });
        });
      });
    });
  }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
    this._subscription.unsubscribe();
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
    this.getTotalInteractions();
    this.getPositiveInteractions();
    this.getUsersReached();
    this.getUsersClickedArticle();
    this.getArticlesSavedToCloset();
    this.getPopularsTags();
    this.getBestTag();
    this.getInteractionsByDay();
    this.getSubcriptors();
    this.getPromOfFeedBack();
    this.sumPositiveInteractions = this.positiveInteractions + this.articlesSavedToCloset + this.usersClickedArticle;
  }

  getTotalInteractions() {
    this.totalInteractions = this.interactions.length;
    console.log(`tolalInteractions: ` + this.totalInteractions);
  }

  getPositiveInteractions() {
    this.positiveInteractions = this.interactions.filter(interaction => interaction.liked === true).length;
    console.log(`positiveInteractions: ` + this.positiveInteractions);
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
      if ((interaction.liked === true || interaction.savedToCloset === true || interaction.clickOnArticle === true)
        && interaction.tags !== null) {
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
    this.bestTag = this.popularityXtag[indexOfMaxValue].tag;
  }


  getInteractionsByDay() {
    this.InteractionsXday = [];
    this.daysOfTheWeek = [];
    this.interactionsByDay = [];
    let index;
    const today = new Date();
    this.interactions.forEach((interaction) => {
      if (interaction.interactionTime !== undefined) {
        const dateOfInteraction: Date = new Date(interaction.interactionTime.toDate());
        if (!this.daysOfTheWeek.includes(`${dateOfInteraction.getDate()}/${dateOfInteraction.getMonth() + 1}`)) {
          this.daysOfTheWeek.push(`${dateOfInteraction.getDate()}/${dateOfInteraction.getMonth() + 1}`);
          this.interactionsByDay.push(0);
        }
        index = this.daysOfTheWeek.indexOf(`${dateOfInteraction.getDate()}/${dateOfInteraction.getMonth() + 1}`);
        this.interactionsByDay[index]++;
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
    let sum = 0;
    for (let i = 0; i < this.feedBack.length ; i++) {
      sum = sum + +this.feedBack[i].feedBack;
      console.log(`sumatoria parcial ` + sum);
    }
    this.feedBackProm = (sum / this.feedBack.length);
    console.log(`feedBackProm ` + this.feedBackProm);
  }

  getAmountOfPromotedInteractions() {
    /*for (let i = 0; i < this.interactions.length ; i++) {
      if ( this.interactions[i] )
    }*/
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


