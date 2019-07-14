import { BrowserModule, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import {
  Component,
  Inject,
  OnInit,
  ViewChild,
  ElementRef,
  OnDestroy,
  ChangeDetectionStrategy
} from '@angular/core';
import {
  AngularFireUploadTask,
  AngularFireStorageReference
} from 'angularfire2/storage';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatChipInputEvent,
  MatAutocompleteSelectedEvent
} from '@angular/material';
import { Observable, Subscription, empty } from 'rxjs';
import { UploadTaskSnapshot } from '@angular/fire/storage/interfaces';
import {
  Validators,
  FormBuilder,
  FormGroup,
  FormControl
} from '@angular/forms';
import { ArticleService } from '../../services/article.service';
import { MatSnackBar } from '@angular/material';
import { finalize, startWith, map } from 'rxjs/operators';
import { Article } from '../../models/article';
import { TagsService } from '../../services/tags.service';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Tags } from '../../models/tags';
import { DataService } from '../../../service/dataService';
import { LyResizingCroppingImages, ImgCropperConfig } from '@alyle/ui/resizing-cropping-images';
import { LyTheme2 } from '@alyle/ui';

const styles = {
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

@Component({
  selector: 'app-article-dialog',
  templateUrl: 'articleDialog.html',
  styleUrls: ['./articleDialog.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ArticleDialogComponent implements OnInit, OnDestroy {
  // cropper settings
  classes = this.theme.addStyleSheet(styles);
  croppedImage?: string[] = ['', '', ''];
  @ViewChild(LyResizingCroppingImages) img: LyResizingCroppingImages;
  result: string;
  myConfig: ImgCropperConfig = {
    width: 150, // Default `250`
    height: 150, // Default `200`,
    output: {
      width: 500,
      height: 500
    }
  };

  isLoadedImage = [false, false, false];
  // taskReference
  ref: AngularFireStorageReference;
  // Main task
  task: AngularFireUploadTask;
  // Progress monitoring
  percentage: Observable<number>;
  snapshot: Observable<UploadTaskSnapshot>;
  // Download url
  downloadURL: Observable<string>;
  isHovering: boolean;
  isNew = true;
  articleForm: FormGroup;
  urls = new Array<String>();
  filesSelected: FileList;

  tags: string[] = [];
  sizes: string[] = [];
  colors: string[] = [];
  _subscription: Subscription;
  allTags: Tags;
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = false;
  isUpLoading = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  filteredTags: Observable<string[]>;

  tagsCtrl = new FormControl();
  sizesCtrl = new FormControl();
  colorsCtrl = new FormControl();
  actualImageId = 0;
  @ViewChild('cropper', undefined)
  // cropper: ImageCropperComponent;
  // cropperSettings: CropperSettings;
  @ViewChild('tagsInput') tagsInput: ElementRef<HTMLInputElement>;
  @ViewChild('sizesInput') sizesInput: ElementRef<HTMLInputElement>;
  @ViewChild('colorsInput') colorsInput: ElementRef<HTMLInputElement>;

  constructor(
    private theme: LyTheme2,
    public tagsService: TagsService,
    public snackBar: MatSnackBar,
    private articleService: ArticleService,
    private dataService: DataService,
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ArticleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public articleData: Article
  ) {
    /*this.cropperSettings = new CropperSettings();
    this.cropperSettings.noFileInput = true;
    this.cropperSettings.croppedWidth = 400;
    this.cropperSettings.croppedHeight = 400;
    this.cropperSettings.canvasHeight = 210;
    this.cropperSettings.canvasWidth = 210;
*/
    this.createForm();
    if (articleData.picture !== undefined) {
      const articlePicture = (this.isNew = false);
    } else {
    }
  }

  ngOnInit(): void {
    try {

      this._subscription = this.tagsService.getTags().subscribe(tags => {
        this.allTags = tags[0];
        this.filteredTags = this.tagsCtrl.valueChanges.pipe(
          startWith(null),
          map((tag: string | null) => {
            if (tag) {
              return this._filter(tag);
            } else if ( this.allTags ) {
              return this.allTags.preset.slice();
            }
          }
          // tag ? this._filter(tag) : this.allTags.preset.slice() el this.allTags venia undefined a veces.
          )
        );
      });
    } catch (error) { console.log(error); }
  }

  ngOnDestroy(): void {
    if (this._subscription) {
      this._subscription.unsubscribe();
    }
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.allTags.preset.filter(
      tags =>
        tags
          .toString()
          .toLowerCase()
          .indexOf(filterValue) === 0
    );
  }

  createForm() {
    if (this.articleData.tags === null) {
      this.articleData.tags = [];
    }
    if (this.articleData.sizes === null) {
      this.articleData.sizes = [];
    }
    if (this.articleData.colors === null) {
      this.articleData.colors = [];
    }
    this.tags = this.articleData.tags;
    this.sizes = this.articleData.sizes;
    this.articleForm = this.fb.group({
      // completar los datos de la prenda
      title: [this.articleData.title, Validators.nullValidator],
      code: [this.articleData.code, Validators.nullValidator],
      cost: [this.articleData.cost, Validators.nullValidator],
      picture: ['', Validators.nullValidator],
      picturesArray: ['', Validators.nullValidator],
      sizes: [this.articleData.sizes.map(x => x), Validators.nullValidator],
      material: [this.articleData.material, Validators.nullValidator],
      colors: [this.articleData.colors.map(x => x), Validators.nullValidator],
      initial_stock: [this.articleData.initial_stock, Validators.nullValidator],
      provider: [this.articleData.provider, Validators.nullValidator],
      tags: [this.articleData.tags.map(x => x), Validators.nullValidator],
      storeName: [this.articleData.storeName, Validators.nullValidator]
    });
  }

  toggleHover(event: boolean) {
    this.isHovering = event;
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 2000
    });
  }

  startUpload() {
    if ( !this.checkImagenLoaded() ) {
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
    this.articleForm.addControl(
      'promotionLevel',
      new FormControl(1, Validators.required)
    );
    this.articleForm.addControl(
      'creationDate',
      new FormControl(new Date(), Validators.required)
    );
    this.articleForm.get('tags').setValue(this.tags.map(x => x));
    this.articleForm.get('sizes').setValue(this.sizes.map(x => x));
    this.articleForm.get('colors').setValue(this.colors.map(x => x));
    this.uploadPictures(imagesToUpload).then(picturesURL => {
      this.articleForm.get('picturesArray').setValue(picturesURL.map(x => x));
      this.articleService.addArticle(this.articleForm.value).then(() => {
        this.isUpLoading = false;
        console.log('prenda guardada');
        this.openSnackBar('Prenda guardada en MyLook!', 'close');
        this.dialogRef.close();
      });
    });
  }

  uploadPictures(items: any[]) {
    return new Promise<any>(resolve => {
      const result = [];
      // tslint:disable-next-line: quotemark
      console.log('items ', items);

      this.dataService.uploadPictureFile(items[0]).then(res0 => {
        console.log('res0 ', res0);
        result.push(res0);
        if (items[1] !== '') {
          this.dataService.uploadPictureFile(items[1]).then(res1 => {
            console.log('res1', res1);
            result.push(res1);
            if (items[2] !== '') {
              this.dataService.uploadPictureFile(items[2]).then(res2 => {
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

  // actualiza la descripcion de una prenda
  refreshArticle(event) {
    const articleUpdated: Article = {
      articleId: this.articleData.articleId,
      title: this.articleData.title,
      cost: this.articleForm.controls['cost'].value,
      sizes: this.articleData.sizes.map(x => x),
      material: this.articleForm.controls['material'].value,
      colors: this.articleData.colors.map(x => x),
      initial_stock: this.articleForm.controls['initial_stock'].value,
      provider: this.articleForm.controls['provider'].value,
      tags: this.articleData.tags.map(x => x),
      storeName: this.articleData.storeName
    };
    this.articleService.refreshArticle(articleUpdated);
    console.log(articleUpdated.tags);
    this.openSnackBar('Prenda actualizada en MyLook!', 'close');
  }

  isActive(snapshot) {
    return (
      snapshot.state === 'running' &&
      snapshot.bytesTransferred < snapshot.totalBytes
    );
  }

  onFileSelected(event) {
    console.log(event);
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  // aniade un nuevo tag a la prenda
  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      this.tags.push(value.trim());
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  addSize(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      this.sizes.push(value.trim());
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
    console.log(this.sizes);
  }

  addColor(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      this.colors.push(value.trim());
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
    console.log(this.colors);
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.tags.push(event.option.viewValue);
    this.tagsInput.nativeElement.value = '';
    this.tagsCtrl.setValue(null);
  }

  remove(tag): void {
    const index = this.tags.indexOf(tag);
    if (index >= 0) {
      this.tags.splice(index, 1);
    }
  }

  selectedSize(event: MatAutocompleteSelectedEvent): void {
    this.sizes.push(event.option.viewValue);
    this.sizesInput.nativeElement.value = '';
    this.sizesCtrl.setValue(null);
  }

  removeSize(size): void {
    const index = this.sizes.indexOf(size);
    if (index >= 0) {
      this.sizes.splice(index, 1);
    }
  }

  removeColor(color): void {
    const index = this.colors.indexOf(color);
    if (index >= 0) {
      this.colors.splice(index, 1);
    }
  }

  /*
  fileChangeListener($event) {
     console.log('el event', event);
    const image: any = new Image();
    const file: File = $event.target.files[0];
    if (file.type.split('/')[1] === 'png') {
      return this.openSnackBar('Tipo de imagen no soportado!', 'close');
    }
    const myReader: FileReader = new FileReader();
    const that = this;
    myReader.onloadend = function(loadEvent: any) {
      image.src = loadEvent.target.result;
      that.cropper.setImage(image);
      // that.data[that.actualImageId] = image;
    };
    myReader.readAsDataURL(file);
  }
*/

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
}
