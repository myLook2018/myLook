import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { Article } from '../../models/article';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ArticleDialogComponent } from '../dialogs/articleDialog';
import { MatDialog, MatTableDataSource, MatSort } from '@angular/material';
import { AuthService } from '../../../auth/services/auth.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ArticleService } from '../../services/article.service';
import { DeleteConfirmationDialogComponent } from '../dialogs/deleteConfirmationDialog';

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
    'tags',
    'actions'
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

  deleteArticle(article) {
    this.articleService.deleteArticle(article);
    console.log(`Articulo ${article.id} eliminado`);
  }

  /*ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }*/

  openConfirmationDialog(article): void {
    const confirmationRef = this.dialog.open(DeleteConfirmationDialogComponent, {
      width: '300px',
      data: article.picture
    });
    confirmationRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.deleteArticle(article);
      }
    });
  }

  openArticleDialog(article: Article): void {
    let dataToSend = {};
    if (article !== undefined) {
        dataToSend = {
        id: article.id,
        picture: article.picture,
        cost: article.cost,
        size: article.size,
        material: article.material,
        colors: article.colors,
        initial_stock: article.initial_stock,
        provider: article.provider,
        tags: article.tags };
        }

    const dialogRef = this.dialog.open(ArticleDialogComponent, {
      height: '610px',
      data: dataToSend
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
