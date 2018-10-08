import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Resolve, Router } from '@angular/router';
import { StoreService } from '../../../auth/services/store.service';
import * as firebase from 'firebase';
import { Subscription } from 'rxjs';
import { Observable } from '../../../../../node_modules/rxjs';
import { MatDialog } from '@angular/material';
import { MapsDialogComponent } from '../../../dialog/maps-dialog/maps-dialog.component';
import { Article } from '../../../articles/models/article';
import { Store } from '../../model/store.model';
import { EditStoreComponent } from '../dialogs/editStore';
import { UserService } from '../../../auth/services/user.service';
import { StoreModel } from '../../../auth/models/store.model';
import { AuthService } from '../../../auth/services/auth.service';
import { Location } from '@angular/common';
import { NgxSpinnerService } from 'ngx-spinner';
// import { ObservableMedia } from '@angular/flex-layout';

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss']
})
export class StoreComponent implements OnInit, OnDestroy {
  storeName: string;
  storeService: StoreService;
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[] = [];
  user = new StoreModel();
  _subscription2: Subscription;
  constructor(
    private route: ActivatedRoute,
    public dialog: MatDialog,
    public userService: UserService,
    private router: Router,
    public authService: AuthService,
    private location: Location,
    private spinner: NgxSpinnerService,
    ) {
    this.storeName = route.snapshot.params['storeName'];
  }

  ngOnInit(): void {
    this.spinner.show();
    this.route.data.subscribe(routeData => {
      const data = routeData['data'];
      if (data) {
        this.FirebaseUser = data;
      }
    });
    console.log(1);
    this._subscription2 = this.userService.getUserInfo(this.FirebaseUser.firebaseUserId).subscribe(userA => {
      this.userStore = userA[0];
      if (this.userStore.profilePh === undefined) {this.userStore.profilePh = this.FirebaseUser.profilePh; }
      console.log(3);
      this.route.data.subscribe(routeData => {
        const articles = routeData['articles'];
        if (articles) {
          this.articles = articles;
        }
        setTimeout(() => {
          /** spinner ends after 5 seconds */
          this.spinner.hide();
        }, 2000);
      });
      this.getUserInfo();
      console.log(4);
    });
  }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
    this._subscription2.unsubscribe();
    }

  openMapDialog(): void {
    if (this.userStore.storePosition) {
      const dialogRef = this.dialog.open(MapsDialogComponent, {
        width: '50%',
        height: '50%',
        data: { data: this.userStore },
        id: 'mapDialog'
      });
    } else {
      const queryLink = encodeURIComponent(this.userStore.storeAddress + ' ' + this.userStore.storeAddressNumber + ', Cordoba, Argentina');
      const url = 'https://www.google.com/maps/search/?api=1&query=' + queryLink;
      const win = window.open(url, '_blank');
      win.focus();
    }

  }

  getArticleImage(articleImage) {
    const storageRef = firebase.storage().ref();
    const spaceRef = storageRef.child('test/' + articleImage);
    spaceRef.getDownloadURL().then(function (url) {
      return url;
    }).catch(function (error) {

    });
  }

  editStoreInfo(): void {
    const dialogRef = this.dialog.open(EditStoreComponent, {
      height: '650px',
      data: { data: this.userStore }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  getUserInfo() {
    this.userService.getCurrentUser().then(
      res => {
        this.user.profilePh = res.photoURL;
        this.user.storeName = res.displayName;
        this.user.provider = res.providerData[0].providerId;
        return;
      }, err => {
        this.router.navigate(['/login']);
      }
    );
  }

  logout() {
    this.authService.doLogout().then(
      res => {
        this.location.back();
      },
      error => {
        console.log('Logout error', error);
        this.router.navigate(['/login']);
      }
    );
  }

  goToInventory() {
    this.router.navigate([`/home`]);
  }


}
