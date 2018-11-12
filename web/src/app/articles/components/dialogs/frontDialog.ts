import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Article } from '../../models/article';

@Component({
    selector: 'app-front-dialog',
    templateUrl: 'frontDialog.html',
})
export class FrontDialogComponent {
    onAdd = new EventEmitter();
    articlesToFront = [];
    constructor(
        public dialogRef: MatDialogRef<FrontDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data) { }

    onNoClick(): void {
        this.dialogRef.close();
    }

    addIdToSelecteds(article: Article) {
        const index = this.articlesToFront.indexOf(article.articleId, 0);
        if (index > -1) {
            this.articlesToFront.splice(index, 1);
        } else {
            this.articlesToFront.push(article.articleId);
        }
        console.log(`estado actual ` + this.articlesToFront);
    }

    sendData() {
        this.onAdd.emit(this.articlesToFront);
        console.log(`sendData ` + this.articlesToFront);
    }

}
