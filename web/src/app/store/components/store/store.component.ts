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
import { ArticleService } from 'src/app/articles/services/article.service';
import { NewStoreService } from '../../service/store.service';
// import { ObservableMedia } from '@angular/flex-layout';

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss']
})
export class StoreComponent implements OnInit, OnDestroy {
  over: any;
  storeName: string;
  storeService: StoreService;
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[] = [];
  user = new StoreModel();
  _subscription2: Subscription;
  constructor(
    public newStoreService: NewStoreService,
    public articleService: ArticleService,
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
      console.log(`trayendo recomendaciones`);
      const data = routeData['data'];
      if (data) {
        this.FirebaseUser = data;
      }
    });
    console.log(1);
    this._subscription2 = this.userService.getUserInfo(this.FirebaseUser.firebaseUserId)
      .subscribe(userA => {
        this.userStore = userA[0];
        console.log(this.userStore);
        if (this.userStore.profilePh === undefined) {
          console.log(3);
          this.userStore.profilePh = this.FirebaseUser.profilePh;
        }
        console.log(4);
        this.articleService.getFrontArticlesCopado(this.userStore.storeName).then((articles) => {
          console.log(articles);
          this.articles = articles;
        setTimeout(() => {
          this.spinner.hide();
        }, 2000);
      });
      console.log(5);
    });
  }

  ngOnDestroy(): void {
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

 /* editStoreInfo(): void {
    const dialogRef = this.dialog.open(EditStoreComponent, {
      height: '650px',
      width: '750px',
      data: { data: this.userStore }
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
*/
  editStoreInfo(): void {
    const editRef = this.dialog.open(EditStoreComponent, {
      width: '650px',
      height: '750px',
      data: this.userStore
    });
    const sub = editRef.componentInstance.onAdd.subscribe((res) => {
      if (res !== undefined) {
        this.sendStoreUpdate(res);
      }
    });
    editRef.afterClosed().subscribe(result => {
      sub.unsubscribe();
    });
  }


  logout() {
    this.authService.doLogout().then(
      res => {
        this.location.back();
      },
      error => {
        this.router.navigate(['/login']);
      }
    );
  }

  sendStoreUpdate(newDats) {
    this.newStoreService.refreshStore(this.userStore.firebaseUID, newDats );
  }

  goToProfile() {
    console.log(`already in profile`);
  }

  goToInventory() {
    this.router.navigate([`/inventory`]);
  }

  goToRecomendations() {
    this.router.navigate([`/recomendations`]);
  }

  goToAnalytics() {
    this.router.navigate([`/analytics`]);
  }


}
