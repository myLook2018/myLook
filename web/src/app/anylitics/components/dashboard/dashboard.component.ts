import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { StoreModel } from 'src/app/auth/models/store.model';
import { UserService } from 'src/app/auth/services/user.service';
import { AuthService } from 'src/app/auth/services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { AnyliticService } from '../services/anylitics.service';
import { Interaction } from '../../model/interaction';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  readyToRender = false;
  totalInteractions;
  positiveInteractions;
  usersReached;
  usersClickedArticle;
  articlesSavedToCloset;
  popularTags;
  popularityOfTags;
  matrix;
  popularityXtag;
  options: FormGroup;
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  _subscription: Subscription;
  interactions: Interaction[];

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
        console.log(this.interactions);
        this.getAnylitics();
        setTimeout(() => {
          this.spinner.hide();
          this.readyToRender = true;
        }, 2000);
      });
      /* this.anyliticService.getAllInteractions(this.userStore.storeName).then((interactionsFb) => {
         console.log(interactionsFb);
         this.interactions = interactionsFb; }).then(() => {
           setTimeout(() => {
             this.spinner.hide();
         }, 2000);
         });*/
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

  getPopularsTags() {
    this.popularityXtag = [];
    this.popularTags = [];
    this.popularityOfTags = [];
    let index;
    this.interactions.forEach((interaction) => {
      if (interaction.liked === true || interaction.savedToCloset === true || interaction.clickOnArticle === true) {
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

}
