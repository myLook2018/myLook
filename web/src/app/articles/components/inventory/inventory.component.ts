import { Component, ViewChild, OnInit, OnDestroy } from '@angular/core';
import { Article } from '../../models/article';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ArticleDialogComponent } from '../dialogs/articleDialog';
import { MatDialog, MatTableDataSource, MatSort } from '@angular/material';
import { AuthService } from '../../../auth/services/auth.service';
import { Location } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { ArticleService } from '../../services/article.service';
import { DeleteConfirmationDialogComponent } from '../dialogs/deleteConfirmationDialog';
import { UserService } from '../../../auth/services/user.service';
import { StoreModel } from '../../../auth/models/store.model';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { PromoteDialogComponent } from '../dialogs/promoteDialog';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})
export class InventoryComponent implements OnInit, OnDestroy {
  @ViewChild(MatSort) sort: MatSort;
  options: FormGroup;
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  articles: Article[];
  _subscription2: Subscription;

  constructor(
    fb: FormBuilder,
    public articleService: ArticleService,
    public dialog: MatDialog,
    public userService: UserService,
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private spinner: NgxSpinnerService,
  ) {
    this.options = fb.group({
      hideRequired: false,
      floatLabel: 'never'
    });
    this.userStore.profilePh = '/assets/noProfilePic.png';
  }

  dataSource;
  displayedColumns: string[] = [
    'picture',
    'title',
    'code',
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
    this.spinner.show();
    this.route.data.subscribe(routeData => {
      const data = routeData['data'];
      if (data) {
        this.FirebaseUser = data;
      }
    });
    this._subscription2 = this.userService.getUserInfo(this.FirebaseUser.firebaseUserId).subscribe(userA => {
      this.userStore = userA[0];
      if (this.userStore.profilePh === '') { this.userStore.profilePh = this.FirebaseUser.profilePh; }
      /*this._subscription = this.articleService.getArticles(this.userStore.storeName).subscribe(articlesFirebase => {
        this.articles = articlesFirebase;
        this.dataSource = new MatTableDataSource(this.articles);
        console.log(this.articles);*/
      this.articleService.getArticlesCopado(this.userStore.storeName).then((articles) => {
        this.dataSource = [];
        console.log(articles);
        this.articles = articles;
        this.dataSource = new MatTableDataSource(this.articles);
      }).then(() => {
        setTimeout(() => {
          this.spinner.hide();
        }, 2000);
      });
    }
    );
  }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
    this._subscription2.unsubscribe();
  }

  deleteArticle(article) {
    this.articleService.deleteArticle(article);
  }

  openPromoteDialog(article): void {
    const promoteRef = this.dialog.open(PromoteDialogComponent, {
      width: '300px',
      data: article
    });
    const sub = promoteRef.componentInstance.onAdd.subscribe((res) => {
      if (res !== undefined) {
        this.promoteArticle(res, article, this.userStore.firebaseUID);
      }
    });
    promoteRef.afterClosed().subscribe(result => {
      console.log(`resutl close ` + result);
      sub.unsubscribe();
    });
  }

  promoteArticle(data, article, storeUID) {
    console.log(data);
    console.log(article);
    this.articleService.promoteArticle(data, article, storeUID);
  }

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
        storeName: this.userStore.storeName,
        title: article.title,
        code: article.code,
        id: article.id,
        picture: article.picture,
        cost: article.cost,
        sizes: article.sizes,
        material: article.material,
        colors: article.colors,
        initial_stock: article.initial_stock,
        provider: article.provider,
        tags: article.tags
      };
    } else {
      dataToSend = {
        storeName: this.userStore.storeName,
        tags: [],
        sizes: [],
        colors: []
      };
    }

    const dialogRef = this.dialog.open(ArticleDialogComponent, {
      maxHeight: 'calc(95vh)',
      data: dataToSend
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  logout() {
    this.authService.doLogout().then(
      res => {
        this.router.navigate(['/login']);
      },
      error => {
        console.log('Logout error', error);
      }
    );
  }

  goToProfile() {
    console.log(`/store/${this.userStore.storeName}`);
    this.router.navigate([`/store/${this.userStore.storeName}`]);
  }

  goToRecomendations() {
    this.router.navigate([`/recomendations`]);
  }

  goToAnalytics() {
    this.router.navigate([`/estadisticas`]);
  }
}
