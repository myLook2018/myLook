import { Component, Inject, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import {
  AngularFireStorage,
  AngularFireUploadTask,
  AngularFireStorageReference
} from 'angularfire2/storage';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatChipInputEvent, MatAutocompleteSelectedEvent } from '@angular/material';
import { Observable, Subscription, empty } from 'rxjs';
import { UploadTaskSnapshot } from 'angularfire2/storage/interfaces';
import { Validators, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from '../../services/article.service';
import { MatSnackBar } from '@angular/material';
import { finalize, startWith, map } from 'rxjs/operators';
import { Article } from '../../models/article';
import { TagsService } from '../../services/tags.service';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Tags } from '../../models/tags';
import { DataService } from '../../../service/dataService';
import { ImageCropperComponent, CropperSettings } from 'ngx-img-cropper';

@Component({
  selector: 'app-article-dialog',
  templateUrl: 'articleDialog.html'
})
export class ArticleDialogComponent implements OnInit, OnDestroy {
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
  croppedImage: any = '';

  tags: string[] = [];
  _subscription: Subscription;
  allTags: Tags;
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = false;
  isUpLoading = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  tagsCtrl = new FormControl();
  filteredTags: Observable<string[]>;
  data: any;

  @ViewChild('cropper', undefined)
  cropper: ImageCropperComponent;
  @ViewChild('tagsInput') tagsInput: ElementRef<HTMLInputElement>;
  cropperSettings: CropperSettings;

  constructor(
    public tagsService: TagsService,
    public snackBar: MatSnackBar,
    private articleService: ArticleService,
    private dataService: DataService,
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ArticleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public articleData: Article
  ) {
    this.cropperSettings = new CropperSettings();
    this.cropperSettings.noFileInput = true;
    this.cropperSettings.croppedWidth =  400;
    this.cropperSettings.croppedHeight =  400;
    this.cropperSettings.canvasHeight = 240;
    this.cropperSettings.canvasWidth = 240;
    this.data = {};
    this.createForm();
    if (articleData.picture !== undefined) {
      this.urls.push(articleData.picture); this.isNew = false;
    } else {
      this.urls.push('/assets/hanger.png');
    }
  }

  ngOnInit(): void {
    this._subscription = this.tagsService.getTags().subscribe(tags => {
      this.allTags = tags[0];
      this.filteredTags = this.tagsCtrl.valueChanges.pipe(startWith(null),
        map((tag: string | null) => tag ? this._filter(tag) : this.allTags.preset.slice()));
    });
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.allTags.preset.filter(tags => tags.toString().toLowerCase().indexOf(filterValue) === 0);
  }

  createForm() {
    if (this.articleData.tags === null) { this.articleData.tags = []; }
    this.tags = this.articleData.tags;
    this.articleForm = this.fb.group({
      // completar los datos de la prenda
      title: [this.articleData.title, Validators.nullValidator],
      cost: [this.articleData.cost, Validators.nullValidator],
      picture: ['', Validators.nullValidator],
      size: [this.articleData.size, Validators.nullValidator],
      material: [this.articleData.material, Validators.nullValidator],
      colors: [this.articleData.colors, Validators.nullValidator],
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
      duration: 2000,
    });
  }

  startUpload() {
    this.isUpLoading = true;
    const origin: string = this.data.image;
    const sub: string = origin.substr(23);
    console.log(1);
    console.log(this.data);
    const imageBlob = this.dataURItoBlob(sub);
    console.log(2);
    const imageFile = new File([imageBlob], this.articleForm.controls['title'].value, { type: 'image/jpeg' });
    this.articleForm.get('tags').setValue(this.tags.map(x => x));
    console.log(this.tags.map(x => x));
    this.dataService.uploadPictureFile(imageFile).then(pictureURL => {
      console.log(3);
      this.articleForm.get('picture').setValue(pictureURL);
      this.articleService.addArticle(this.articleForm.value).then(() => {
        this.isUpLoading = false;
        this.openSnackBar('Prenda guardada en MyLook!', 'close');
      });
    });
  }

  // actualiza la descripcion de una prenda
  refreshArticle(event) {
    const articleUpdated: Article = {
      id: this.articleData.id,
      title: this.articleData.title,
      cost: this.articleForm.controls['cost'].value,
      size: this.articleForm.controls['size'].value,
      material: this.articleForm.controls['material'].value,
      colors: this.articleForm.controls['colors'].value,
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
  detectFiles(event) {
    this.filesSelected = event.target.files;
    this.urls = [];
    const files = event.target.files;
    if (files) {
      for (const file of files) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.urls.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }
  }
  fileChangeListener($event) {
    const image: any = new Image();
    const file: File = $event.target.files[0];
    const myReader: FileReader = new FileReader();
    const that = this;
    myReader.onloadend = function (loadEvent: any) {
      image.src = loadEvent.target.result;
      that.cropper.setImage(image);

    };

    myReader.readAsDataURL(file);
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

}
