import { Component } from '@angular/core';
import { MatTableDataSource, MatSnackBar } from '@angular/material';
import { FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { ArticleService } from '../../../services/article.service';
import { Observable } from 'rxjs';
import { DataService } from 'src/app/service/dataService';
import { StoreModel } from 'src/app/auth/models/store.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-storefront',
  templateUrl: 'storeFront.html',
  styleUrls: ['./storeFront.scss'],
})

export class StoreFrontComponent {

  isLoading = false;
  disableButton = false;
  // information: any;
  storefrontForm;
  selectedStorefrontArticles: any;
  options = [];
  filteredOptions: Observable<string[]>;
  dataSource: any;
  displayedColumns: string[] = [
    // 'picture',
    'code',
    'title',
    'actions'
  ];
  selectedIndexes = [];
  actualStore: StoreModel;
  actualArticles: any;
  isUpLoading = false;
  storefrontArray: FormArray;
  selectedStorefrontIndex: any;
  activeStorefront: any;
  filterText = '';
  tooltipMessage = 'Agrega una nueva vidriera';
  deleteTooltip = 'Elimina vidriera';

  constructor(
    private articleService: ArticleService,
    private dataService: DataService,
    private router: Router,
    private formBuilder: FormBuilder,
    public snackBar: MatSnackBar
    // @Inject(MAT_DIALOG_DATA) public data
    ) {
      this.storefrontForm = this.formBuilder.group({
        storefronts: this.formBuilder.array([])
      });
       this.storefrontArray = this.storefrontForm.get('storefronts') as FormArray;
        // this.information = data;
        this.dataService.getStoreInfo().then(store => {
          this.actualStore = store;
          console.log('actual store', this.actualStore);
          let counterIndex = 0;
          this.actualStore.storefronts.forEach(storefront => {
            this.storefrontArray.push(this.createItem(storefront.name, storefront.isActive, storefront.articlesId));
            if (storefront.isActive) {
              this.selectedStorefrontArticles = storefront.articlesId;
              this.activeStorefront = storefront.name;
              this.selectedStorefrontIndex = counterIndex;
            }
            counterIndex++;
          });
          console.log('formArray', this.storefrontArray);
          this.articleService.getArticlesCopado(this.actualStore.storeName).then( articles => {
            this.actualArticles = articles;
            this.dataSource = new MatTableDataSource(this.actualArticles);
            console.log('this.dataSources', this.dataSource);
            this.populateSelectedIndexes();
          });
        });
    }


  onNoClick(): void {
    console.log('me cancelaron');
  }

  sendData() {

  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.options.filter(option => option.name.toLowerCase().indexOf(filterValue) === 0);
  }

  isInSelected(article) {
    return this.selectedIndexes.find(x => x === article.articleId);
  }

  addIdToSelecteds(element, event) {
    console.log('row', element);
    console.log('event', event);
    const index = this.selectedIndexes.indexOf(element.articleId, 0);
    if (index > -1) {
      this.selectedIndexes.splice(index, 1);
    } else {
      this.selectedIndexes.push(element.articleId);
    }
    console.log(`estado actual ` + this.selectedIndexes);
    console.log('this.array ', this.storefrontArray.value);
    console.log('this.selectedStorefrontIndex ', this.selectedStorefrontIndex);
    this.storefrontArray.controls[this.selectedStorefrontIndex].get('articlesId').setValue(this.selectedIndexes);
    console.log('this.array ', this.storefrontArray.value);
  }

  private populateSelectedIndexes() {
    this.actualArticles.forEach(article => {
      if (article.isStorefront) {this.selectedIndexes.push(article.articleId); }
    });
    console.log(`estado actual ` + this.selectedIndexes);
  }

  onOptionSelected( event ) {
    console.log('elegimos esto', event.value);
    if (event.value !== undefined) {
      this.selectedIndexes = this.storefrontArray.value.find(storefront => storefront.name === event.value).articlesId;
    } else { this.selectedIndexes = []; }
    console.log('nuevos indices', this.selectedIndexes);
  }

  goToInventory() {
    this.router.navigate([`/Tiendas/${this.actualStore.storeName}/Catalogo`]);
  }

  submitChanges() {
    console.log('formulario', this.storefrontArray.value);
    this.isUpLoading = true;
    this.actualArticles.forEach(article => {
      this.articleService.refreshVidrieraAttribute(article.articleId, false);
      console.log('punse en false a', article.firebaseUID);
    });

    this.selectedIndexes.forEach( articleID => {
      this.articleService.refreshVidrieraAttribute(articleID, true);
      console.log('punse en true a', articleID);

    });
    this.articleService.updateStorefront(this.actualStore.firebaseUID, this.storefrontArray.value).then(() => {
      this.openSnackBar('Vidriera actualizada en MyLook!', '');
      this.isUpLoading = false;
    });
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 3000
    });
  }

  createItem(nameNew= 'Nueva vidriera', isActiveNew = false, articlesIdNew = []): FormGroup {
    this.selectedIndexes = [];
    return this.formBuilder.group({
      name: [nameNew],
      isActive: [isActiveNew],
      articlesId: [articlesIdNew]
    });
  }

  deleteItem() {
    console.log('habria que borrar el indice ', this.selectedStorefrontIndex);
    if (this.selectedStorefrontIndex !== null) {
      this.storefrontArray.removeAt(this.selectedStorefrontIndex);
    }
    console.log('nos queda', this.storefrontArray.value);
    this.selectedStorefrontIndex = null;
    this.activeStorefront = null;
    this.selectedIndexes = [];
  }

  setIndex(index) {
    this.selectedStorefrontIndex = index;
    this.storefrontArray.controls.forEach( x => x.get('isActive').setValue(false));
    console.log(' this.storefrontArray.controls.length', this.storefrontArray.controls.length);
    console.log('this.selectedStorefrontIndex', this.selectedStorefrontIndex);
    this.storefrontArray.controls[this.selectedStorefrontIndex].get('isActive').setValue(true);
    console.log('indice en ', this.selectedStorefrontIndex);
  }

  addNewStoreFront() {
    this.storefrontArray.push(this.createItem());
    this.activeStorefront = 'Nueva vidriera';
    this.setIndex(this.storefrontArray.controls.length - 1);
  }

  filterArticles(event) {
    console.log('tengo este input', event);
    // console.log('articulos');
    const newArticles = this.actualArticles.filter( article => (article.code.toLowerCase().includes(event.toLowerCase()) ||
        article.title.toLowerCase().includes(event.toLowerCase()))
      );
    this.dataSource = new MatTableDataSource(newArticles);

  }

  changeStorefrontName(event) {
    console.log('this.storefrontArray', this.storefrontArray);
    console.log('this.index', this.selectedStorefrontIndex);
    console.log('event', event);
    this.storefrontArray.controls[this.selectedStorefrontIndex].get('name').setValue(event);
    this.activeStorefront = event;
  }

  isEmptyFront() {
    const result = this.storefrontArray.controls.find(x => x.get('name').value === 'Nueva vidriera');
    if (result ) {
      this.tooltipMessage = 'Debes cambiar el nombre a "Nueva vidriera" antes de agregar otra.';
      return true;
    } else {
      this.tooltipMessage = 'Agrega una nueva vidriera';
      return false;
    }
  }

  checkFrontFull(element, $event) {
    console.log('laskmdlkasdmlaksmdlkmsadlm');
    if (!this.isInSelected(element) && (this.selectedIndexes.length > 5)) {
      this.openSnackBar('Solo puede seleccionar hasta 6 prendas por vidriera.', '');
    }
  }
}
