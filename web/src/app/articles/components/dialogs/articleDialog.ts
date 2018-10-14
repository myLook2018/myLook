import { Component, Inject, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import {
  AngularFireStorage,
  AngularFireUploadTask,
  AngularFireStorageReference
} from 'angularfire2/storage';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatChipInputEvent, MatAutocompleteSelectedEvent } from '@angular/material';
import { Observable, Subscription } from 'rxjs';
import { UploadTaskSnapshot } from 'angularfire2/storage/interfaces';
import { Validators, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from '../../services/article.service';
import { MatSnackBar } from '@angular/material';
import { finalize, startWith, map } from 'rxjs/operators';
import { Article } from '../../models/article';
import { TagsService } from '../../services/tags.service';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { Tags } from '../../models/tags';
import { DataService } from '../../../service/dataService';

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

  tags: string[] = [];
  _subscription: Subscription;
  allTags: Tags;
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  tagsCtrl = new FormControl();
  filteredTags: Observable<string[]>;
  @ViewChild('tagsInput') tagsInput: ElementRef<HTMLInputElement>;

  constructor(
    public tagsService: TagsService,
    public snackBar: MatSnackBar,
    private articleService: ArticleService,
    private dataService: DataService,
    private fb: FormBuilder,
    private storage: AngularFireStorage,
    public dialogRef: MatDialogRef<ArticleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public articleData: Article
    ) { this.createForm();
    if (articleData.picture !== undefined ) {
      this.urls.push(articleData.picture); this.isNew = false;
     } else { this.urls.push('/assets/hanger.png'); }
  }

  ngOnInit(): void {
    this._subscription = this.tagsService.getTags().subscribe(tags => {
      console.log(tags[0]);
      this.allTags = tags[0];
      console.log(this.allTags.preset.length);
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
    this.articleForm = this.fb.group({
      // completar los datos de la prenda
      cost: [this.articleData.cost, Validators.nullValidator],
      size: [this.articleData.size, Validators.nullValidator],
      material: [this.articleData.material, Validators.nullValidator],
      colors: [this.articleData.colors, Validators.nullValidator],
      initial_stock: [this.articleData.initial_stock, Validators.nullValidator],
      provider: [this.articleData.provider, Validators.nullValidator],
      tags: [this.articleData.tags, Validators.nullValidator],
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
    this.dataService.uploadPicture(this.filesSelected).then(pictureURL => {
      this.articleForm.addControl('picture', new FormControl(pictureURL, Validators.required));
      console.log('añadimos al form');
      console.log(pictureURL); // <-- do what ever you want with the url..
      this.articleService.addArticle(this.articleForm.value).then(() => {
        this.openSnackBar('Prenda guardada en MyLook!', 'close');
      });
    });
  }

  // actualiza la descripcion de una tienda
  refreshArticle(event) {
    const articleUpdated: Article = {
      id: this.articleData.id,
      cost: this.articleForm.controls['cost'].value,
      size: this.articleForm.controls['size'].value,
      material: this.articleForm.controls['material'].value,
      colors: this.articleForm.controls['colors'].value,
      initial_stock: this.articleForm.controls['initial_stock'].value,
      provider: this.articleForm.controls['provider'].value,
      tags: this.articleForm.controls['tags'].value,
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

  // Genera el preview de las imagenes a cargar
  detectFiles(event) {
    this.filesSelected = event.target.files;
    this.urls = [];
    const files = event.target.files;
    if (files) {
      for (const file of files) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          console.log(this.urls.length);
          console.log(file);
          this.urls.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }
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
}
