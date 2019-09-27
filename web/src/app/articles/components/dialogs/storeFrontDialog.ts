import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatHorizontalStepper, MatTableDataSource } from '@angular/material';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from './../../services/article.service';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

@Component({
  selector: 'app-storefront-dialog',
  templateUrl: 'storeFrontDialog.html',
  styleUrls: ['./storeFrontDialog.scss'],
})

export class StoreFrontDialogComponent {

  isLoading = false;
  disableButton = false;
  information: any;
  storefrontControl = new FormControl();
  options: string[] = ['One', 'Two', 'Three'];
  filteredOptions: Observable<string[]>;
  dataSource: any;
  displayedColumns: string[] = [
    // 'picture',
    'code',
    'title',
    'actions'
  ];
  selectedIndexes = [];

  constructor(
    public dialogRef: MatDialogRef<StoreFrontDialogComponent>,
    private articleService: ArticleService,
    @Inject(MAT_DIALOG_DATA) public data
    ) { this.information = data;
        console.log('la data en el modal ', this.information);
        this.dataSource = new MatTableDataSource(this.information.articles);
        console.log('this.dataSources', this.dataSource);
        this.filteredOptions = this.storefrontControl.valueChanges.pipe(startWith(''), map(value => this._filter(value))
        );
        this.populateSelectedIndexes();
    }


  onNoClick(): void {
    this.dialogRef.close();
    console.log('me cancelaron');
  }

  sendData() {

  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.options.filter(option => option.toLowerCase().indexOf(filterValue) === 0);
  }

  isInSelected(article) {
    return this.selectedIndexes.find(x => x === article.articleId);
  }

  addIdToSelecteds(row, event) {
    console.log('row',row);
    console.log('event',event);
    const index = this.selectedIndexes.indexOf(this.information.articles[row].articleId, 0);
    if (index > -1) {
      this.selectedIndexes.splice(index, 1);
    } else {
      this.selectedIndexes.push(this.information.articles[row].articleId);
    }
    console.log(`estado actual ` + this.selectedIndexes);
  }

  private populateSelectedIndexes() {
    this.information.articles.forEach(article => {
      if (article.isStorefront) {this.selectedIndexes.push(article.articleId) }
    });
    console.log(`estado actual ` + this.selectedIndexes);
  }

}
