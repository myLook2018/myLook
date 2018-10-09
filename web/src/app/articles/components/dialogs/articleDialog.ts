import { Component, Inject, OnInit, ViewChild, ElementRef } from '@angular/core';
import {
  AngularFireStorage,
  AngularFireUploadTask,
  AngularFireStorageReference
} from 'angularfire2/storage';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatChipInputEvent, MatAutocompleteSelectedEvent } from '@angular/material';
import { Observable } from 'rxjs';
import { UploadTaskSnapshot } from 'angularfire2/storage/interfaces';
import { Validators, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from '../../services/article.service';
import { MatSnackBar } from '@angular/material';
import { finalize, startWith, map } from 'rxjs/operators';
import { Article } from '../../models/article';
import { TagsService } from '../../services/tags.service';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { Tags } from '../../models/tags';

@Component({
  selector: 'app-article-dialog',
  templateUrl: 'articleDialog.html'
})
export class ArticleDialogComponent implements OnInit {
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
    this.tagsService.getTags().subscribe(tags => {
      console.log(tags[0]);
      this.allTags = tags[0];
      console.log(this.allTags.preset.length);
      this.filteredTags = this.tagsCtrl.valueChanges.pipe(startWith(null),
      map((tag: string | null) => tag ? this._filter(tag) : this.allTags.preset.slice()));
    });
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.allTags.preset.filter(tags => tags.toString().toLowerCase().indexOf(filterValue) === 0);
  }

  createForm() {
    this.articleForm = this.fb.group({
      // completar los datos de la imagen
      cost: [this.articleData.cost, Validators.nullValidator],
      size: [this.articleData.size, Validators.nullValidator],
      material: [this.articleData.material, Validators.nullValidator],
      colors: [this.articleData.colors, Validators.nullValidator],
      initial_stock: [this.articleData.initial_stock, Validators.nullValidator],
      provider: [this.articleData.provider, Validators.nullValidator],
      tags: [this.articleData.tags, Validators.nullValidator],
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
    // First item
    const file = this.filesSelected.item(0);

    // Client side validation
    if (file.type.split('/')[0] !== 'image') {
      console.log('Tipo de imagen no soportado.');
      return;
    }

    // The storage Path (must be unique)
    const path = `test/${new Date().getTime()}_${file.name}`;
    // Optional metadata
    const customMetadata = { app: 'Mylook!' };
    // The main task / metadata is optional
    console.log('path: ' + path);
    this.task = this.storage.upload(path, file, { customMetadata }); // suuubiendo

    console.log('Imagen guardada en myLook!');
    // Progress monitoring
    // this.percentage = this.task.percentageChanges(); // usar si es necesario
    // this.snapshot = this.task.snapshotChanges(); // cambio de version

    // The file download URL
    // cambio de version/implementacion

    console.log('a ver el link');
    this.ref = this.storage.ref(path);

    this.task.snapshotChanges().pipe(
      finalize(() => {
        this.ref.getDownloadURL().subscribe(url => {
          this.articleForm.addControl('picture', new FormControl(url, Validators.required));
          console.log('añadimos al form');
          this.articleService.addArticle(this.articleForm.value);
          console.log(url); // <-- do what ever you want with the url..
        });
      })
    ).subscribe();


    console.log('pasamos el add');
    this.openSnackBar('Prenda guardada en MyLook!', 'close');

  }

  refreshArticle(event) {
    const articleUpdated: Article = {
      id: this.articleData.id,
      cost: this.articleForm.controls['cost'].value,
      size: this.articleForm.controls['size'].value,
      material: this.articleForm.controls['material'].value,
      colors: this.articleForm.controls['colors'].value,
      initial_stock: this.articleForm.controls['initial_stock'].value,
      provider: this.articleForm.controls['provider'].value,
      tags: this.articleForm.controls['tags'].value
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

  detectFiles(event) {
    this.filesSelected = event.target.files;
    this.urls = [];
    const files = event.target.files;
    if (files) {
      for (const file of files) {
        console.log('pasamos a render');
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

    this.tagsCtrl.setValue(null);
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.tags.push(event.option.viewValue);
    this.tagsInput.nativeElement.value = '';
    this.tagsCtrl.setValue(null);
  }
}
