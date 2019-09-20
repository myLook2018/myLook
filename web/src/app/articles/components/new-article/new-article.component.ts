import { OnInit, OnDestroy, Component, ViewEncapsulation, ViewChild,  } from '@angular/core';
import { FormGroup, FormBuilder, Validators} from '@angular/forms';
import { SPACE, ENTER, COMMA } from '@angular/cdk/keycodes';
import { MatChipInputEvent, MatSnackBar } from '@angular/material';
import { LyResizingCroppingImages, ImgCropperConfig } from '@alyle/ui/resizing-cropping-images';
import { LyTheme2 } from '@alyle/ui';
import { DataService } from 'src/app/service/dataService';
import { ArticleService } from '../../services/article.service';
import { StoreModel } from 'src/app/auth/models/store.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-new-article',
  templateUrl: './new-article.component.html',
  styleUrls: ['./new-article.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class NewArticleComponent implements OnInit, OnDestroy {
  isNew = true;
  articleForm: FormGroup;
  separatorKeysCodes: number[] = [ENTER, COMMA, SPACE];
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
  // ------ Fin cosas de las imagenes --------

  constructor (
    private formBuilder: FormBuilder,
    public snackBar: MatSnackBar,
    private dataService: DataService,
    private articleService: ArticleService,
    private router: Router,
    private theme: LyTheme2 ) {
  }

  ngOnInit() {
    this.createForm();
    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
    });
  }

  ngOnDestroy(): void {
  }

  createForm() {
    this.articleForm = this.formBuilder.group({
      code: ['', Validators.required],
      colors: [[]],
      cost: ['', Validators.required],
      creationDate: [new Date, Validators.required],
      estaEnVidriera: [false],
      initial_stock: [, Validators.required],
      material: ['', Validators.required],
      picturesArray: [[]],
      promotionLevel: [1],
      provider: ['', Validators.required],
      sizes: [],
      storeLatitude: [],
      storeLongitude: [],
      storeName: [],
      tags: [[]],
      title: ['', Validators.required]
    });
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

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000
    });
  }

  goToInventory() {
    this.router.navigate([`/Tiendas/${this.actualStore.storeName}/Catalogo`]);
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
      if(this.isLoadedImage[index]) {
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
  // ----------- Fin Metodos de cortar imagenes  ---------------------------
  // ----------- Inicia el metodo para cargar la prenda  ---------------------------
  startUpload() {
    this.isUpLoading = true;
    if ( !this.checkImagenLoaded() ) {
      this.isUpLoading = false;
      this.snackBar.open('Es necesario que cargue al menos una imagen de la prenda para poder continuar.', '', {
        duration: 3000,
        panelClass: ['blue-snackbar']
      });
      return;
    }

    this.isUpLoading = true;
    console.log('las imagenes ', this.croppedImage);
    let imagesToUpload: File[] = [];
    this.croppedImage.forEach(photo => {
      //delete
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
      this.articleService.addArticle(this.articleForm.value).then(() => {
        this.isUpLoading = false;
        console.log('prenda guardada');
        this.openSnackBar('Prenda guardada en MyLook!', 'x');
      });
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
}
