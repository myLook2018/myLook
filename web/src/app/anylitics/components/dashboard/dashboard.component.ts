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
      if (this.userStore.profilePh === '') { this.userStore.profilePh = this.FirebaseUser.profilePh; 
      this.anyliticService.getArticlesCopado(this.userStore.storeName).then((interactionsFb) => {
        // limpiar sources
        console.log(interactionsFb);
        this.interactions = interactionsFb;});
      }
      setTimeout(() => {
        this.spinner.hide();
      }, 2000);
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

}
