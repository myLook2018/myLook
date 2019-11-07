import { Component, OnInit, ViewChild, AfterViewInit, NgZone, ElementRef } from '@angular/core';
import { DataService } from '../../../service/dataService';
import { StoreModel } from 'src/app/auth/models/store.model';
import { FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import { ImgCropperConfig, LyResizingCroppingImages } from '@alyle/ui/resizing-cropping-images';
import { MatSnackBar, MatDialog } from '@angular/material';
import { NewStoreService } from '../../service/store.service';
import { MapsAPILoader } from '@agm/core';
import { Router } from '@angular/router';
import { ChangePasswordDialogComponent } from '../dialogs/change-password/change-password-dialog';
// import { StoreService } from 'src/app/auth/services/store.service';

@Component({
  selector: 'app-configurations',
  templateUrl: './configurations.component.html',
  styleUrls: ['./configurations.component.scss']
})
export class ConfigurationsComponent implements OnInit, AfterViewInit {
  @ViewChild('cropping0') cropping0: LyResizingCroppingImages;
  @ViewChild('cropping1') cropping1: LyResizingCroppingImages;

  @ViewChild('search')
  public searchElementRef: ElementRef;

  isUpLoading = false;
  actualStore = new StoreModel();
  actualFirebaseUser;
  myFormGroup: FormGroup;
  isEditMode = true;
  myConfig: ImgCropperConfig = {
    width: 486, // Default `250`
    height: 486, // Default `200`,
    output: {
      width: 500,
      height: 500
    }
  };
  myConfigPortada: ImgCropperConfig = {
    width: 735, // Default `250`
    height: 245, // Default `200`,
    output: {
      width: 1500,
      height: 500
    }
  };
  croppedImage?: string[] = ['', ''];
  isLoadedImage = [false, false];
  actualImageId: any;

  title = 'My first AGM project';
  latitude: number;
  longitude: number;
  zoom: number;
  address: string;
  private geoCoder;

  // currentPassword = new FormControl('');
  // newPassword = new FormControl('');



  constructor(  private dataService: DataService,
                private formBuilder: FormBuilder,
                public snackBar: MatSnackBar,
                private newStoreService: NewStoreService,
                private mapsAPILoader: MapsAPILoader,
                private ngZone: NgZone,
                private router: Router,
                public dialog: MatDialog,

                // private storeService: StoreService
    ) {
   }

  ngOnInit() {

    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
      this.actualFirebaseUser = this.dataService.getFirebaseUser();
      // this.getPromotionsDone();
      console.log('+-'.repeat(15), this.actualStore);
      console.log('+-'.repeat(15), this.actualFirebaseUser);
      this.myFormGroup = this.formBuilder.group({
        profilePh: [{value: this.actualStore.profilePh, disabled: this.isUpLoading}, Validators.nullValidator],
        coverPh: [{value: this.actualStore.coverPh, disabled: this.isUpLoading}, Validators.nullValidator],
        storeLongitude: [{value: this.actualStore.storeLongitude, disabled: this.isUpLoading}, Validators.nullValidator],
        storeLatitude: [{value: this.actualStore.storeLatitude, disabled: this.isUpLoading}, Validators.nullValidator],
        storeName: [{value: this.actualStore.storeName, disabled: this.isUpLoading}, Validators.nullValidator],
        storeAddress: [{value: this.actualStore.storeAddress, disabled: this.isUpLoading}, Validators.nullValidator],
        storeTower: [{value: this.actualStore.storeTower, disabled: this.isUpLoading}, Validators.nullValidator],
        storeFloor: [{value: this.actualStore.storeFloor, disabled: this.isUpLoading}, Validators.nullValidator],
        storeDept: [{value: this.actualStore.storeDept, disabled: this.isUpLoading}, Validators.nullValidator],
        storePhone: [{value: this.actualStore.storePhone, disabled: this.isUpLoading}, Validators.nullValidator],
        facebookLink: [{value: this.actualStore.facebookLink, disabled: this.isUpLoading}, Validators.nullValidator],
        instagramLink: [{value: this.actualStore.instagramLink, disabled: this.isUpLoading}, Validators.nullValidator],
        twitterLink: [{value: this.actualStore.twitterLink, disabled: this.isUpLoading}, Validators.nullValidator],
        storeDescription: [{value: this.actualStore.storeDescription, disabled: this.isUpLoading}, Validators.nullValidator]
      });
      setTimeout( () => {
        this.mapsAPILoader.load().then(() => {
          this.latitude = this.actualStore.storeLatitude;
          this.longitude = this.actualStore.storeLongitude;
          this.zoom = 15;
          // this.setCurrentLocation();
          this.geoCoder = new google.maps.Geocoder();

          const autocomplete = new google.maps.places.Autocomplete(
            this.searchElementRef.nativeElement,
            {
              types: ['address']
            }
          );
          autocomplete.addListener('place_changed', () => {
            this.ngZone.run(() => {
              // get the place result
              const place: google.maps.places.PlaceResult = autocomplete.getPlace();

              // verify result
              if (place.geometry === undefined || place.geometry === null) {
                return;
              }
              console.log('place', place);

              // set latitude, longitude and zoom
              this.myFormGroup.get('storeAddress').setValue( place.formatted_address);
              this.latitude = place.geometry.location.lat();
              this.longitude = place.geometry.location.lng();
              this.zoom = 15;
            });
          });
        });
        this.loadImagesToCrop();
      }, 1000);
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
      }, 1000);
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
    const croppers = [this.cropping0, this.cropping1];
    if (this.actualStore.profilePh) {
      console.log('cargando indice imagen');
      croppers[0].setImageUrl(this.actualStore.profilePh);
        this.isLoadedImage[0] = this.actualStore.profilePh === '';
        console.log('cargamos la foto');
      }
    if (this.actualStore.coverPh) {
      console.log('cargando indice imagen');
      croppers[1].setImageUrl(this.actualStore.coverPh);
        this.isLoadedImage[1] = this.actualStore.coverPh === '';
        console.log('cargamos la coverPh');
      }
    }

    cropImages(crop1, crop2) {
      const croppers = [crop1, crop2];
      console.log('crops', croppers);
      for (let index = 0; index < this.isLoadedImage.length; index++) {
        if (this.isLoadedImage[index]) {
          try {
            croppers[index].crop();
          } catch (error) {
            console.log(error);
          }
        }
      }
      this.startUpload();
    }

    startUpload() {
      this.isUpLoading = true;
      this.disableForm();
      if ( !this.checkImagenLoaded() ) {
        this.enableForm();
        this.isUpLoading = false;
        this.snackBar.open('Es necesario que cargue una foto de perfíl y una de portada para continuar.', '', {
          duration: 3000,
          panelClass: ['blue-snackbar']
        });
        return;
      }

      this.isUpLoading = true;
      this.disableForm();
      console.log('las imagenes ', this.croppedImage);
      const imagesToUpload: File[] = [];
      this.croppedImage.forEach(photo => {
        // delete
        console.log('photo ', photo);
        const sub: string = photo.substr(22);
        console.log('sub ', sub);
        const imageBlob = this.dataURItoBlob(sub);
        const imageFile = new File(
          [imageBlob],
          this.actualStore.storeName,
          { type: 'image/jpeg' }
        );
        imagesToUpload.push(imageFile);
      });
      this.uploadPictures(imagesToUpload).then(picturesURL => {
        this.myFormGroup.get('profilePh').setValue(picturesURL[0]);
        this.myFormGroup.get('coverPh').setValue(picturesURL[1]);
        this.myFormGroup.get('storeLatitude').setValue( this.latitude);
        this.myFormGroup.get('storeLongitude').setValue( this.longitude);
          this.newStoreService.refreshStore( this.actualStore.firebaseUID, this.myFormGroup.getRawValue()).then( () => {
            this.snackBar.open('Su Información ha sido actualizada', '', {
              duration: 3000,
              panelClass: ['blue-snackbar']});
            this.isUpLoading = false;
            this.enableForm();
          });
        }
      );
    }

    uploadPictures(items: any[]) {
      return new Promise<any>(resolve => {
        const result = [];
        // tslint:disable-next-line: quotemark
        console.log('items ', items);

        // eliminar fotos que estan vacias
        const realItems = [];
        items.forEach(file => {
          if (file.size > 0) {
            realItems.push(file);
          }
        });

        console.log('realItems', realItems);

        this.dataService.uploadPictureFile(realItems[0]).then(res0 => {
          console.log('res0 ', res0);
          result.push(res0);
          if (realItems[1]) {
            this.dataService.uploadPictureFile(realItems[1]).then(res1 => {
              console.log('res1', res1);
              result.push(res1);
              if (realItems[2]) {
                this.dataService.uploadPictureFile(realItems[2]).then(res2 => {
                  console.log('res2', res2);
                  result.push(res2);
                  resolve(result);
                });
              } else {
                resolve(result);
              }
            });
          } else {
            resolve(result);
          }
        });
      });
    }

    checkImagenLoaded() {
      let result = 0;
      this.croppedImage.forEach(image => {
        console.log('la imagen', image);
        if (image) {
          result++;
        }
      });
      return result === 2;
    }

    dataURItoBlob(dataURI) {
      const byteString = atob(dataURI);
      const arrayBuffer = new ArrayBuffer(byteString.length);
      const int8Array = new Uint8Array(arrayBuffer);

      for (let i = 0; i < byteString.length; i++) {
        int8Array[i] = byteString.charCodeAt(i);
      }
      const blob = new Blob([arrayBuffer], { type: 'image/jpeg' });
      return blob;
    }
  // ----------------------------------- Fin todo lo de cortar imagenes --------------------------
  // Get Current Location Coordinates
  private setCurrentLocation() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(position => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 15;
      });
    }
  }

  markerDragEnd($event: any) {
    console.log($event);
    this.latitude = $event.coords.lat;
    this.longitude = $event.coords.lng;
    this.getAddress(this.latitude, this.longitude);
  }

  getAddress(latitude, longitude) {
    this.geoCoder.geocode({ 'location': { lat: latitude, lng: longitude } }, (results, status) => {
      console.log(results);
      console.log(status);
      if (status === 'OK') {
        if (results[0]) {
          this.zoom = 15;
          this.myFormGroup.get('storeAddress').setValue( results[0].formatted_address);
          this.myFormGroup.get('storeLatitude').setValue( this.latitude);
          this.myFormGroup.get('storeLongitude').setValue( this.longitude);
        } else {
          window.alert('No results found');
        }
      } else {
        window.alert('Geocoder failed due to: ' + status);
      }

    });
  }

  disableForm () {
    this.myFormGroup.get('storeAddress').disable();
    this.myFormGroup.get('storeTower').disable();
    this.myFormGroup.get('storeFloor').disable();
    this.myFormGroup.get('storeDept').disable();
    this.myFormGroup.get('storePhone').disable();
    this.myFormGroup.get('facebookLink').disable();
    this.myFormGroup.get('instagramLink').disable();
    this.myFormGroup.get('twitterLink').disable();
    this.myFormGroup.get('storeDescription').disable();
  }
  enableForm () {
    this.myFormGroup.get('storeAddress').enable();
    this.myFormGroup.get('storeTower').enable();
    this.myFormGroup.get('storeFloor').enable();
    this.myFormGroup.get('storeDept').enable();
    this.myFormGroup.get('storePhone').enable();
    this.myFormGroup.get('facebookLink').enable();
    this.myFormGroup.get('instagramLink').enable();
    this.myFormGroup.get('twitterLink').enable();
    this.myFormGroup.get('storeDescription').enable();
    this.myFormGroup.get('storeDescription').enable();
  }

  goToProfile() {
    this.router.navigate(['Tiendas', this.actualStore.storeName]);
  }

  // changePassword() {
    // this.storeService.changePassword('123456');
  // }

  openChangePasswordDialog() {
    const dataToSend = {
      storeEmail: this.actualStore.storeMail,
    };

    this.dialog.open(ChangePasswordDialogComponent, {
      width: '450px',
      disableClose: true,
      data: dataToSend
    });
  }
}
