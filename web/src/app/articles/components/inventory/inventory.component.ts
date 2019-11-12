import { Component, ViewChild, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Article } from '../../models/article';
import { FormGroup, FormBuilder } from '@angular/forms';
import {
  MatDialog,
  MatTableDataSource,
  MatSort,
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
import { ToastsService, TOASTSTYPES } from 'src/app/service/toasts.service';

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
  // storeFront: FormGroup;
  articlesToGenerateFront: Article[] = [];
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[];
  selectionMode = false;
  selectedIndexes = [];
  articlesSubscription: Subscription;
  constructor(
    public fb: FormBuilder,
    public articleService: ArticleService,
    public dataService: DataService,
    public dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute,
    private toastsService: ToastsService
  ) {
    // this.createForm();
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
    // 'size',
    // 'material',
    'colors',
    // 'initial_stock',
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

  // createForm() {
  //   this.storeFront = this.fb.group({
  //     // completar los datos de la prenda
  //   });
  // }

  // deleteForm() {
  //   this.storeFront = undefined;
  // }

  deleteArticle(article) {
    this.articleService.deleteArticle(article);
  }

  openPromoteDialog(article, event): void {
    event.stopPropagation();
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

  openConfirmationDialog(article, event): void {
    event.stopPropagation();
    const confirmationRef = this.dialog.open(
      DeleteConfirmationDialogComponent,
      {
        width: '425px',
        data: {
          photo: article.picturesArray[0],
          title: article.title,
          promotionLevel: article.promotionLevel
        }
      }
    );
    confirmationRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteArticle(article);
        this.toastsService.showToastMessage('Borrado exitoso', TOASTSTYPES.SUCCESS, 'Se ha borrado la prenda de su catálogo');
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
    this.toastsService.showToastMessage('Vidriera actualizada', TOASTSTYPES, 'Se ha actualizado su vidriera con éxito.');
    this.selectionMode = false;
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
    console.log('row', row);
    console.log('event', event);
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

  stop(event) {
    event.stopPropagation();
  }

  showInformation(article) {
    if (this.selectionMode) {
      return console.log('evite redireccion');
    }
    console.log('Yendo a ver articulos', `/Tiendas/${this.userStore.storeName}/Nuevo-Articulo/${article.articleId}`);
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Ver-Articulo/${article.articleId}`]);
  }

  goToEdit(article) {
    event.stopPropagation();
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Editar-Articulo/${article.articleId}`]);
  }

  goToAddArticle() {
    console.log('Yendo a ver articulos', `/Tiendas/${this.userStore.storeName}/Nuevo-Articulo/`);
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Nuevo-Articulo`]);
  }

  goToStoreFront() {
    console.log('Yendo a elegir vidriera');
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Vidriera`]);
  }
}
