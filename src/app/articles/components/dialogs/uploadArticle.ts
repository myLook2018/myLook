import { Component, Inject } from '@angular/core';
import {MatDialog, MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
@Component({
    selector: 'app-uploadarticle',
    templateUrl: 'uploadArticle.html',
  })
  export class UpLoadArticleComponent {
    constructor(
      public dialogRef: MatDialogRef<UpLoadArticleComponent>,
      @Inject(MAT_DIALOG_DATA) article: any ) {

    }
    onFileSelected(event) {
      console.log(event);
    }

    onNoClick(): void {
      this.dialogRef.close();
    }
  }

