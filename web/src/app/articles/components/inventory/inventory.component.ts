import { Component, ViewChild, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Article } from '../../models/article';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ArticleDialogComponent } from '../dialogs/articleDialog';
import {
  MatDialog,
  MatTableDataSource,
  MatSort,
  MatSnackBar,
  MatSortable
} from '@angular/material';
import { Router } from '@angular/router';
import { ArticleService } from '../../services/article.service';
import { DeleteConfirmationDialogComponent } from '../dialogs/deleteConfirmationDialog';
import { StoreModel } from '../../../auth/models/store.model';
import { PromoteDialogComponent } from '../dialogs/promoteDialog';
import { FrontDialogComponent } from '../dialogs/frontDialog';
import { DataService } from 'src/app/service/dataService';
import { Subscription } from 'rxjs';

declare var Mercadopago: any;
@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit, OnDestroy {
  @ViewChild(MatSort) sort: MatSort;
  sortOrder = 'asc';
  over: any;
  options: FormGroup;
  storeFront: FormGroup;
  articlesToGenerateFront: Article[] = [];
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[];
  selectionMode = false;
  selectedIndexes = [];
  articlesSubscription: Subscription;
  constructor(
    public snackBar: MatSnackBar,
    public fb: FormBuilder,
    public articleService: ArticleService,
    public dataService: DataService,
    public dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.createForm();
    this.options = fb.group({
      hideRequired: false,
      floatLabel: 'never'
    });
    this.userStore.profilePh = '/assets/noProfilePic.png';
  }

  dataSource;
  displayedColumns: string[] = [
    'picture',
    'title',
    'code',
    'cost',
    'size',
    'material',
    'colors',
    'initial_stock',
    'tags',
    'actions'
  ];

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  ngOnInit() {
    if (this.route.snapshot.paramMap.get('collection_status')) {
      const isAprovedPay = this.route.snapshot.paramMap.get('collection_status');
      const articleId = this.route.snapshot.paramMap.get('external_reference');
      console.log('este es el external ID ', articleId);
      console.log('es aprobado? ', isAprovedPay);
    }
    console.log('-+-+-+-+-+-Inicializando Inventario-+-+-+-+-+-');
    this.dataSource = [];
    this.dataService.getStoreInfo().then(store => {
      this.userStore = store;
      this.articlesSubscription = this.articleService
      .getArticles(this.userStore.storeName).subscribe( articles => {
          // console.log(articles);
          this.articles = articles.filter(Boolean);
          console.log(articles);
          this.dataSource = new MatTableDataSource(this.articles);
          this.sort.sort(<MatSortable>{ id: 'promotionLevel', start: 'desc' });
          this.dataSource.sort = this.sort;
        });
    });
  }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
    if (this.articlesSubscription) {
      this.articlesSubscription.unsubscribe();
    }
  }

  createForm() {
    this.storeFront = this.fb.group({
      // completar los datos de la prenda
    });
  }

  deleteForm() {
    this.storeFront = undefined;
  }

  deleteArticle(article) {
    this.articleService.deleteArticle(article);
  }

  openPromoteDialog(article): void {
    if (article.promotionLevel !== 1) {
      console.log('pito me voy a abrir');
      return;
    }
    const dataToSend = {
      storeName: this.userStore.storeName,
      phone: this.userStore.storePhone,
      phoneArea: '2966',
      ownerName: this.userStore.ownerName,
      storeEmail: this.userStore.storeMail,
      dni: 38773582,
      title: article.title,
      code: article.code,
      id: article.articleId,
      picture: article.picturesArray[0],
      cost: article.cost,
      sizes: article.sizes,
      material: article.material,
      colors: article.colors,
      initial_stock: article.initial_stock,
      provider: article.provider,
      tags: article.tags
    };
    const promoteRef = this.dialog.open(PromoteDialogComponent, {
      width: '750px',
      data: article,
      disableClose: true
    });
    const sub = promoteRef.componentInstance.onAdd.subscribe(res => {
      if (res !== undefined) {
        this.promoteArticle(res, article, this.userStore.firebaseUID);
        // this.RedirectToMercadoPago(res);
      }
    });
    promoteRef.afterClosed().subscribe(result => {
      console.log(`resutl close ` + result);
      sub.unsubscribe();
    });
  }

  promoteArticle(data, article, storeUID) {
    console.log(data);
    console.log(article);
    this.articleService.promoteArticle(data, article, storeUID);
  }

  openConfirmationDialog(article): void {
    const confirmationRef = this.dialog.open(
      DeleteConfirmationDialogComponent,
      {
        width: '300px',
        data: article.picturesArray[0]
      }
    );
    confirmationRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteArticle(article);
      }
    });
  }

  openFrontDialog(): void {
    const frontRef = this.dialog.open(FrontDialogComponent, {
      data: this.articles
    });
    const sub = frontRef.componentInstance.onAdd.subscribe(res => {
      if (res !== undefined) {
        console.log(`devolcimos ` + res);
        this.setVidriera(res);
        // action this.promoteArticle(res, article, this.userStore.firebaseUID);
      }
    });
    frontRef.afterClosed().subscribe(result => {
      console.log(`resutl close ` + result);
      sub.unsubscribe();
    });
  }

  openArticleDialog(article: Article): void {
    let dataToSend = {};
    if (article !== undefined) {
      dataToSend = {
        storeName: this.userStore.storeName,
        storeLatitude: this.userStore.storeLatitude,
        storeLongitude: this.userStore.storeLongitude,
        title: article.title,
        code: article.code,
        id: article.articleId,
        picturesArray: article.picturesArray,
        cost: article.cost,
        sizes: article.sizes,
        material: article.material,
        colors: article.colors,
        initial_stock: article.initial_stock,
        provider: article.provider,
        tags: article.tags
      };
    } else {
      dataToSend = {
        storeName: this.userStore.storeName,
        storeLatitude: this.userStore.storeLatitude,
        storeLongitude: this.userStore.storeLongitude,
        tags: [],
        sizes: [],
        colors: []
      };
    }

    const dialogRef = this.dialog.open(ArticleDialogComponent, {
      maxWidth: '850px',
      maxHeight: 'calc(95vh)',
      data: dataToSend
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  resetSelectedVidriera() {
    for (let i = 0; i < this.articles.length; i++) {
      console.log(`sacando de vidriera ` + this.articles[i].title);
      this.articleService.refreshVidrieraAttribute(
        this.articles[i].articleId,
        false
      );
    }
  }

  setVidriera(idOfFrontsArticles: string[] =  this.selectedIndexes) {
    this.resetSelectedVidriera();
    for (let i = 0; i < idOfFrontsArticles.length; i++) {
      console.log(`ahora ponemos en vidriera a ` + idOfFrontsArticles[i]);
      this.articleService.refreshVidrieraAttribute(idOfFrontsArticles[i], true);
    }
    this.articlesToGenerateFront = [];
    this.openSnackBar('Su vidriera ha sido actualizada!', 'x');
    this.selectionMode = false;
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000
    });
  }

  createSubCollection() {
    for (let i = 0; i < this.selectedIndexes.length; i++) {
      const index = this.selectedIndexes[i];
      this.articlesToGenerateFront.push(this.articles[index]);
    }
    this.articleService.addStoreFront(
      this.userStore.storeName,
      this.articlesToGenerateFront,
      1
    );
    this.articlesToGenerateFront = [];
  }

  addIdToSelecteds(row, event) {
    console.log('row',row);
    console.log('event',event);
    const index = this.selectedIndexes.indexOf(this.articles[row].articleId, 0);
    if (index > -1) {
      this.selectedIndexes.splice(index, 1);
    } else {
      this.selectedIndexes.push(this.articles[row].articleId);
    }
    console.log(`estado actual ` + this.selectedIndexes);
  }

  changeMode() {
    this.selectionMode = !this.selectionMode;
    this.selectedIndexes = [];
  }

  goToProfile() {
    console.log(`/store/${this.userStore.storeName}`);
    this.router.navigate([`/home`]);
    console.log(this.userStore.profilePh);
  }

  goToInventory() {
    console.log(`already in inventory`);
  }

  goToRecomendations() {
    this.router.navigate([`/recomendations`]);
  }

  goToAnalytics() {
    this.router.navigate([`/analytics`]);
  }

  cancelSetting() {
    this.selectionMode = false;
    this.selectedIndexes = [];
  }

  // RedirectToMercadoPago(promData) {
  //   switch (promData.promotionCost) {
  //     case 10: {
  //       window.open('https://www.mercadopago.com/mla/checkout/start?pref_id=181044052-8b71c605-305a-44b5-8328-e07bb750ea94');
  //       break;
  //     }
  //   }
  // }
}
