import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Resolve } from '@angular/router';
import { StoreService } from '../../../auth/services/store.service';
import * as firebase from 'firebase';
import { Observable } from '../../../../../node_modules/rxjs';
import { MatDialog } from '@angular/material';
import { MapsDialogComponent } from '../../../dialog/maps-dialog/maps-dialog.component';
import { Article } from '../../../articles/models/article';
import { Store } from '../../model/store.model';
import { EditStoreComponent } from '../dialogs/editStore';
// import { ObservableMedia } from '@angular/flex-layout';

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss']
})
export class StoreComponent implements OnInit {
  storeName: string;
  storeService: StoreService;
  storeData: Store;
  articles: Article[];
  constructor(private route: ActivatedRoute, public dialog: MatDialog) {
    this.storeName = route.snapshot.params['storeName'];
  }

  ngOnInit(): void {
    this.route.data.subscribe(routeData => {
      const data = routeData['data'];
      if (data) {
        this.storeData = data;
      }

    });
    this.route.data.subscribe(routeData => {
      const articles = routeData['articles'];
      if (articles) {
        this.articles = articles;
      }
    });
  }

  openMapDialog(): void {
    if (this.storeData.storePosition) {
      const dialogRef = this.dialog.open(MapsDialogComponent, {
        width: '50%',
        height: '50%',
        data: { data: this.storeData },
        id: 'mapDialog'
      });
    } else {
      const queryLink = encodeURIComponent(this.storeData.storeAddress + ' ' + this.storeData.storeAddressNumber + ', Cordoba, Argentina');
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
      data: { data: this.storeData },
      width: '450px'
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

}
