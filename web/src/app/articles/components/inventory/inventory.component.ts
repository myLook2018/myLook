import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { Article } from '../../models/article';
import { FormGroup, FormBuilder } from '@angular/forms';
import { UpLoadArticleComponent } from '../dialogs/uploadArticle';
import { MatDialog, MatTableDataSource, MatSort } from '@angular/material';
import { AuthService } from '../../../auth/services/auth.service';
import { Location } from '@angular/common';
import { Router } from '../../../../../node_modules/@angular/router';
import { ArticleService } from '../../services/article.service';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit {
  options: FormGroup;
  @ViewChild(MatSort) sort: MatSort;
  articles: Article[];
  constructor(
    fb: FormBuilder,
    public articleService: ArticleService,
    public dialog: MatDialog,
    public authService: AuthService,
    private location: Location,
    private router: Router
  ) {
    this.options = fb.group({
      hideRequired: false,
      floatLabel: 'never'
    });
  }
  dataSource;

  displayedColumns: string[] = [
    'picture',
    'cost',
    'size',
    'material',
    'colors',
    'initial_stock',
    'tags'
  ];

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  ngOnInit() {
    this.articleService.getArticles().subscribe(articles => {
      console.log(articles);
      this.articles = articles;
      console.log(this.articles.length);
      this.dataSource = new MatTableDataSource(this.articles);
    }
  );
  }

  /*ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }*/

  openDialog(): void {
    const dialogRef = this.dialog.open(UpLoadArticleComponent, {
      height: '650px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  logout() {
    this.authService.doLogout().then(
      res => {
        this.location.back();
      },
      error => {
        console.log('Logout error', error);
        this.router.navigate(['/login']);
      }
    );
  }
}
