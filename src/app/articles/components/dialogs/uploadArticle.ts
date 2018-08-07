import { Component, Inject } from '@angular/core';
import {
  AngularFireStorage,
  AngularFireUploadTask
} from 'angularfire2/storage';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Observable } from 'rxjs';
import { UploadTaskSnapshot } from 'angularfire2/storage/interfaces';
import { inspectNativeElement } from '@angular/platform-browser/src/dom/debug/ng_probe';
import { Validators, FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from '../../services/article.service';
@Component({
  selector: 'app-uploadarticle',
  templateUrl: 'uploadArticle.html'
})
export class UpLoadArticleComponent {
  // Main task
  task: AngularFireUploadTask;
  // Progress monitoring
  percentage: Observable<number>;
  snapshot: Observable<UploadTaskSnapshot>;
  // Download url
  downloadURL: Observable<string>;
  isHovering: boolean;
  articleForm: FormGroup;
  urls = new Array<string>();
  filesSelected: FileList;

  constructor(
    private articleService: ArticleService,
    private fb: FormBuilder,
    private storage: AngularFireStorage,
    public dialogRef: MatDialogRef<UpLoadArticleComponent>,
    @Inject(MAT_DIALOG_DATA) article: any
  ) { this.createForm(); }

  createForm() {
    this.articleForm = this.fb.group({
      // completar los datos de la imagen
      cost: ['', Validators.nullValidator],
      size: ['', Validators.nullValidator],
      material: ['', Validators.nullValidator],
      colors: ['', Validators.nullValidator],
      initial_stock: ['', Validators.nullValidator],
      provider: ['', Validators.nullValidator],
      tags: ['', Validators.nullValidator],
    });
  }

  toggleHover(event: boolean) {
    this.isHovering = event;
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
    this.articleForm.addControl('picture', new FormControl(path, Validators.required));
    // Optional metadata
    const customMetadata = { app: 'Mylook!' };
    // The main task / metadata is optional
    console.log('path: ' + path);
    this.task = this.storage.upload(path, file, { customMetadata }); // suuubiendo

    console.log('Imagen guardada en myLook!');
    // Progress monitoring
    this.percentage = this.task.percentageChanges(); // usar si es necesario
    this.snapshot = this.task.snapshotChanges(); // cambio de version

    // The file download URL
    // cambio de version
    this.articleService.addItem(this.articleForm.value);
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
}
