import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { DataService } from '../../../service/dataService';
import { StoreModel } from 'src/app/auth/models/store.model';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import { ImgCropperConfig, LyResizingCroppingImages } from '@alyle/ui/resizing-cropping-images';
import { PromotionsService } from './service/promotions.service';
import { MatTableDataSource } from '@angular/material';
@Component({
  selector: 'app-configurations',
  templateUrl: './configurations.component.html',
  styleUrls: ['./configurations.component.scss']
})
export class ConfigurationsComponent implements OnInit, AfterViewInit{
  @ViewChild('cropping0') cropping0: LyResizingCroppingImages;

  actualStore = new StoreModel();
  actualFirebaseUser;
  myFormGroup: FormGroup;
  isEditMode = false;
  myConfig: ImgCropperConfig = {
    width: 400, // Default `250`
    height: 400, // Default `200`,
    output: {
      width: 500,
      height: 500
    }
  };
  croppedImage?: string[] = ['', '', ''];
  isLoadedImage = [false];
  actualImageId: any;

  displayedColumns = ['Articulo', 'FechaInicio', 'FechaFin', 'NivelPromocion', 'PrecioFinal'];
  promotionTableDataSource: any;
  promotionsData;
  constructor( private dataService: DataService, private formBuilder: FormBuilder, private promotionsService: PromotionsService) {
   }

  ngOnInit() {
    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
      this.actualFirebaseUser = this.dataService.getFirebaseUser();
      this.getPromotionsDone();
      console.log('+-'.repeat(15), this.actualStore);
      console.log('+-'.repeat(15), this.actualFirebaseUser);
      this.myFormGroup = this.formBuilder.group({
        storeAddress: [{value: this.actualStore.storeAddress, disabled: !this.isEditMode}, Validators.nullValidator],
        storeTower: [{value: this.actualStore.storeTower, disabled: !this.isEditMode}, Validators.nullValidator],
        storeFloor: [{value: this.actualStore.storeFloor, disabled: !this.isEditMode}, Validators.nullValidator],
        storeDept: [{value: this.actualStore.storeDept, disabled: !this.isEditMode}, Validators.nullValidator],
        storePhone: [{value: this.actualStore.storePhone, disabled: !this.isEditMode}, Validators.nullValidator],
        facebookLink: [{value: this.actualStore.facebookLink, disabled: !this.isEditMode}, Validators.nullValidator],
        instagramLink: [{value: this.actualStore.instagramLink, disabled: !this.isEditMode}, Validators.nullValidator],
        twitterLink: [{value: this.actualStore.twitterLink, disabled: !this.isEditMode}, Validators.nullValidator],
        storeDescription: [{value: this.actualStore.storeDescription, disabled: !this.isEditMode}, Validators.nullValidator]
      });
    });
  }

  ngAfterViewInit() {
  }

  changeMode($event = true) {
    this.isEditMode = !this.isEditMode;
    console.log('+-'.repeat(15), this.actualStore);
    console.log('+-'.repeat(15), this.actualFirebaseUser);
    this.myFormGroup = this.formBuilder.group({
      storeAddress: [{value: this.actualStore.storeAddress, disabled: !this.isEditMode}, Validators.nullValidator],
      storeTower: [{value: this.actualStore.storeTower, disabled: !this.isEditMode}, Validators.nullValidator],
      storeFloor: [{value: this.actualStore.storeFloor, disabled: !this.isEditMode}, Validators.nullValidator],
      storeDept: [{value: this.actualStore.storeDept, disabled: !this.isEditMode}, Validators.nullValidator],
      storePhone: [{value: this.actualStore.storePhone, disabled: !this.isEditMode}, Validators.nullValidator],
      facebookLink: [{value: this.actualStore.facebookLink, disabled: !this.isEditMode}, Validators.nullValidator],
      instagramLink: [{value: this.actualStore.instagramLink, disabled: !this.isEditMode}, Validators.nullValidator],
      twitterLink: [{value: this.actualStore.twitterLink, disabled: !this.isEditMode}, Validators.nullValidator],
      storeDescription: [{value: this.actualStore.storeDescription, disabled: !this.isEditMode}, Validators.nullValidator]
    });
    if (this.isEditMode) {
      setTimeout( () => {
        this.loadImagesToCrop();
      }, 100);
    }
  }

  updateInformation($event) {
    this.changeMode();
  }

  // ------------------------------------- todo lo de cortar imagenes --------------------------
  oncropped(e, index) {
    console.log(`cropped `, e);
    console.log(`cropped index`, index);
    this.croppedImage[index] = e.dataURL;
  }
  onloaded(index) {
    console.log('img loaded');
    this.isLoadedImage[index] = true;
    this.onSelectedImage(index);
  }
  onerror() {
    console.warn('img not loaded');
  }
  onSelectedImage(index) {
    console.log('se esta subiendo al indice ', index);
    this.actualImageId = index;
  }

  doClean(index) {
    console.log('img cleared');
    this.isLoadedImage[index] = false;
  }

  loadImagesToCrop() {
    const croppers = [this.cropping0];
    if (this.actualStore.profilePh) {
      console.log('cargando indice imagen');
      croppers[0].setImageUrl(this.actualStore.profilePh);
        this.isLoadedImage[0] = this.actualStore.profilePh === '';
      }
    }
  // ----------------------------------- Fin todo lo de cortar imagenes --------------------------

  getPromotionsDone() {
    this.promotionsService.getArticlesCopado(this.actualStore.firebaseUID).then( promotions => {
      console.log('promotions que me corresponden', promotions);
      this.promotionsData = promotions;
      this.promotionTableDataSource = new MatTableDataSource(this.promotionsData);
    });
  }

  getTotalCost() {
    return this.promotionsData.map(promotion => promotion.promotionCost).reduce((acc, value) => acc + value, 0);
  }

  getPromotionLevel(level) {
    return (level === 2) ? 'Premium' : 'Estandar';
  }
}
