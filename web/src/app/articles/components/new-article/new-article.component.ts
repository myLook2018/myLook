import { OnInit, OnDestroy, Component, ViewEncapsulation, ViewChild, AfterViewInit,  } from '@angular/core';
import { FormGroup, FormBuilder, Validators} from '@angular/forms';
import { SPACE, ENTER, COMMA } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material';
import { LyResizingCroppingImages, ImgCropperConfig } from '@alyle/ui/resizing-cropping-images';
import { LyTheme2 } from '@alyle/ui';
import { DataService } from 'src/app/service/dataService';
import { ArticleService } from '../../services/article.service';
import { StoreModel } from 'src/app/auth/models/store.model';
import { Router, Route, ActivatedRoute } from '@angular/router';
import { ToastsService, TOASTSTYPES} from 'src/app/service/toasts.service';

@Component({
  selector: 'app-new-article',
  templateUrl: './new-article.component.html',
  styleUrls: ['./new-article.component.scss'],
  // encapsulation: ViewEncapsulation.None
})

export class NewArticleComponent implements OnInit, OnDestroy, AfterViewInit {
  isNew = true;
  articleForm: FormGroup;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  colors = [];
  sizes = [];
  tags = [];

  // ------ cosas de las imagenes --------
  styles = {
    actions: {
      display: 'flex'
    },
    cropping: {
      maxWidth: '400px',
      height: '150px'
    },
    flex: {
      flex: 1
    }
  };
  isLoadedImage = [false, false, false];
  actualImageId = 0;
  // cropper settings
  classes = this.theme.addStyleSheet(this.styles);
  croppedImage?: string[] = ['', '', ''];
  // @ViewChild(LyResizingCroppingImages) img: LyResizingCroppingImages;
  @ViewChild('cropping0') cropping0: LyResizingCroppingImages;
  @ViewChild('cropping1') cropping1: LyResizingCroppingImages;
  @ViewChild('cropping2') cropping2: LyResizingCroppingImages;
  @ViewChild('cropper', undefined)

  result: string;
  myConfig: ImgCropperConfig = {
    width: 250, // Default `250`
    height: 250, // Default `200`,
    output: {
      width: 500,
      height: 500
    }
  };
  isUpLoading: boolean;
  actualStore: StoreModel;
  viewArticleId: string;
  dashboardTitle = 'Cargá los Datos de tu Prenda';
  addPhotoLabel = 'Añadir foto';
  sendButtonLabel = 'Añadir prenda';
  isFormDisabled = false;
  isEditMode: boolean;
  cancelLabel = 'Volver';
  photosLabel = 'Fotos';
  // ------ Fin cosas de las imagenes --------

  constructor (
    private formBuilder: FormBuilder,
    public toastService: ToastsService,
    private dataService: DataService,
    private articleService: ArticleService,
    private router: Router,
    private route: ActivatedRoute,
    private theme: LyTheme2 ) {
      this.viewArticleId = this.route.snapshot.paramMap.get('id');
      this.isEditMode = this.router.url.includes('Editar');
      this.isNew = this.router.url.includes('Nuevo');
      this.photosLabel = this.isNew ? 'Seleccioná las fotos' : 'Fotos';
      console.log('es editar? ', this.isEditMode);
      console.log('el id que nos mandan', this.viewArticleId);
      if (this.viewArticleId) {
        this.dashboardTitle = 'Datos de tu Prenda';
        this.addPhotoLabel = '';
        this.isFormDisabled = true;
        this.createForm();
        this.articleService.getSingleArticle(this.viewArticleId).then(articleData => {
          console.log('el article recibido ', articleData);
          this.articleForm.patchValue(articleData);
          this.loadChipsValues(articleData);
          const pictureArray = articleData.picturesArray;
          this.loadImagesToCrop(pictureArray);
          if (this.isEditMode) { this.changeToEdit(); }
        });
      } else { this.createForm(); }
  }

  ngOnInit() {
    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
    });
  }

  ngAfterViewInit() {
    const pictureArray = this.articleForm.get('picturesArray').value;
    this.loadImagesToCrop(pictureArray);
  }

  ngOnDestroy(): void {
  }

  createForm() {
    this.articleForm = this.formBuilder.group({
      code: [{value: '', disabled: this.isFormDisabled}, Validators.required],
      colors: [{value: [], disabled: this.isFormDisabled}],
      cost: [{value: '', disabled: this.isFormDisabled}, Validators.required],
      creationDate: [new Date, Validators.required],
      initial_stock: [{value: null, disabled: this.isFormDisabled}, Validators.required],
      material: [{value: '', disabled: this.isFormDisabled}, Validators.required],
      picturesArray: [[]],
      promotionLevel: [{value: 1, disabled: this.isFormDisabled}],
      provider: [{value: '', disabled: this.isFormDisabled}, Validators.required],
      sizes: [{value: [], disabled: this.isFormDisabled}],
      storeLatitude: [],
      storeLongitude: [],
      storeName: [],
      tags: [{value: [], disabled: this.isFormDisabled}],
      title: [{value: '', disabled: this.isFormDisabled}, Validators.required],
      isStoreFront: [ false, Validators.required],
    });
  }

  loadChipsValues(articleData) {
    this.articleForm.get('tags').setValue('');
    this.articleForm.get('sizes').setValue('');
    this.articleForm.get('colors').setValue('');
    this.tags = articleData.tags;
    this.sizes = articleData.sizes;
    this.colors = articleData.colors;
  }

  getErrorMessage(error) {
    console.log('new-article: error: ', error);
    return 'tengo errores';
  }

  removeElement(elemento, coleccion ): void {
    const index = coleccion.indexOf(elemento);
    if (index >= 0) {
      coleccion.splice(index, 1);
    }
  }

  addElement(event: MatChipInputEvent, coleccion): void {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      coleccion.push(value.trim());
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
    console.log(coleccion);
  }

  goToInventory() {
    this.router.navigate([`/Tiendas/${this.actualStore.storeName}/Catalogo`]);
  }

  changeToEdit() {
    console.log('cambiando labels');
    this.cancelLabel = 'Cancelar';
    this.photosLabel = 'Seleccioná las fotos';
    this.articleForm.enable();
    this.viewArticleId = null;
    this.addPhotoLabel = 'Añadir foto';
    this.dashboardTitle = 'Carga los Datos de tu Prenda';
    this.isEditMode = true;
    this.sendButtonLabel = 'Actualizar prenda';
  }

  addComa(array) {
    console.log('tengo que implementar esto');
  }

  // ----------- Inician Metodos de cortar imagenes ------------------------
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

  setImageID(id: number) {
    this.actualImageId = id;
  }

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

  cropImages(crop1, crop2, crop3) {
    const croppers = [crop1, crop2, crop3];
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

  // loadImagesToCrop() {
  //   const croppers = [this.cropping0, this.cropping1, this.cropping2];
  //   if (this.articleData.picturesArray) {
  //     for (let index = 0; index < this.articleData.picturesArray.length; index++) {
  //       console.log('cargando indice:', index);
  //       croppers[index].setImageUrl(this.articleData.picturesArray[index]);
  //       this.isLoadedImage[index] = this.articleData.picturesArray[index];
  //     }
  //   }
  // }

  checkImagenLoaded() {
    let result = false;
    this.croppedImage.forEach(image => {
      console.log('la imagen', image);
      if (image) {
        result = true;
        return result;
      }
    });
    return result;
  }

  loadImagesToCrop(pictureArray) {
    console.log('loading esto', pictureArray);
    const croppers = [this.cropping0, this.cropping1, this.cropping2];
    if (pictureArray) {
      for (let index = 0; index < pictureArray.length; index++) {
        console.log('cargando indice:', index);
          croppers[index].setImageUrl(pictureArray[index]);
          this.isLoadedImage[index] = pictureArray[index];
      }
    }
  }
  // ----------- Fin Metodos de cortar imagenes  ---------------------------
  // ----------- Inicia el metodo para cargar la prenda  ---------------------------
  async startUpload() {
    this.isUpLoading = true;
    if ( !this.checkImagenLoaded() ) {
      this.isUpLoading = false;
      this.toastService.showToastMessage(
        'Imagen requerida', TOASTSTYPES.ERROR, 'Es necesario que cargue al menos una imagen de la prenda para poder continuar.'
        );
      return;
    }

    const isCodeUsed = await this.checkIfArticleCodeExists(this.articleForm.get('code').value);

    if (isCodeUsed.length > 0 && !this.isEditMode) {
      this.isUpLoading = false;
      this.toastService.showToastMessage(
        'Código en uso', TOASTSTYPES.ERROR, 'El código de la prenda ya se encuentra en uso. Por favor, seleccione uno diferente.'
        );
      this.articleForm.get('code').setErrors({ notUnique: true});
      return;
    }

    this.disableAllInputs();

    this.isUpLoading = true;
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
        this.articleForm.controls['title'].value,
        { type: 'image/jpeg' }
      );
      imagesToUpload.push(imageFile);
    });
    this.articleForm.get('tags').setValue(this.tags.map(x => x));
    this.articleForm.get('sizes').setValue(this.sizes.map(x => x));
    this.articleForm.get('colors').setValue(this.colors.map(x => x));
    this.articleForm.get('storeName').setValue(this.actualStore.storeName);
    this.articleForm.get('storeLatitude').setValue(this.actualStore.storeLatitude);
    this.articleForm.get('storeLongitude').setValue(this.actualStore.storeLongitude);
    this.uploadPictures(imagesToUpload).then(picturesURL => {
      this.articleForm.get('picturesArray').setValue(picturesURL.map(x => x));
      if ( this.isEditMode ) {
        this.articleService.refreshArticle( this.articleForm.getRawValue(), this.route.snapshot.paramMap.get('id')).then( () => {
          this.toastService.showToastMessage(
            'Prenda Actualizada', TOASTSTYPES.SUCCESS, 'La prenda fue actualizada correctamente en myLook.'
            );
          this.isUpLoading = false;
        });
      } else {
        this.articleService.addArticle(this.articleForm.getRawValue()).then(() => {
          this.isUpLoading = false;
          console.log('prenda guardada');
          this.toastService.showToastMessage('Prenda guardada', TOASTSTYPES.SUCCESS, 'La prenda fue guardada correctamente en myLook.');
          this.resetForm();
        });
      }
    });
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
  // ----------- Fin el metodo para cargar la prenda  ------------------------------

  resetForm() {
    this.createForm();
    this.sizes = [];
    this.colors = [];
    this.tags = [];
    this.doClean(0);
    this.doClean(1);
    this.doClean(2);
    const croppers = [this.cropping0, this.cropping1, this.cropping2];
      for (let index = 0; index < croppers.length; index++) {
        console.log('cargando indice:', index);
        croppers[index].clean();
      }
    this.viewArticleId = null;
    this.articleForm.reset();
    this.enableAllInputs();
    }

    disableAllInputs() {
      this.articleForm.get('code').disable();
      this.articleForm.get('colors').disable();
      this.articleForm.get('cost').disable();
      this.articleForm.get('initial_stock').disable();
      this.articleForm.get('material').disable();
      this.articleForm.get('provider').disable();
      this.articleForm.get('sizes').disable();
      this.articleForm.get('tags').disable();
      this.articleForm.get('title').disable();
      this.viewArticleId = 'newID';
    }

    enableAllInputs() {
      this.articleForm.get('code').enable();
      this.articleForm.get('colors').setValue('');
      this.articleForm.get('colors').enable();
      this.articleForm.get('cost').enable();
      this.articleForm.get('initial_stock').enable();
      this.articleForm.get('material').enable();
      this.articleForm.get('provider').enable();
      this.articleForm.get('sizes').setValue('');
      this.articleForm.get('sizes').enable();
      this.articleForm.get('tags').setValue('');
      this.articleForm.get('tags').enable();
      this.articleForm.get('title').enable();
    }

    checkIfArticleCodeExists( articleCode ) {
      return this.articleService.getArticleByCode( this.actualStore.storeName , articleCode);
    }
}
