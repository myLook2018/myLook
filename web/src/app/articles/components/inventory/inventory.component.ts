import { Component, ViewChild, OnInit, OnDestroy} from '@angular/core';
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
  _subscription: Subscription;
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
        if (this.userStore.profilePh === '') {this.userStore.profilePh = this.FirebaseUser.profilePh; }
        this._subscription = this.articleService.getArticles(this.userStore.storeName).subscribe(articles => {
          this.articles = articles;
          this.dataSource = new MatTableDataSource(this.articles);
          setTimeout(() => {
            this.spinner.hide();
          }, 2000);
        }
        );
      }
        );
    }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
    this._subscription.unsubscribe();
    this._subscription2.unsubscribe();
    }

deleteArticle(article) {
      this.articleService.deleteArticle(article);
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
        id: article.id,
        picture: article.picture,
        cost: article.cost,
        size: article.size,
        material: article.material,
        colors: article.colors,
        initial_stock: article.initial_stock,
        provider: article.provider,
        tags: article.tags };
        } else {
        dataToSend = { storeName: this.userStore.storeName};
        }

    const dialogRef = this.dialog.open(ArticleDialogComponent, {
      height: '630px',
      data: dataToSend
    });

    dialogRef.afterClosed().subscribe(result => {
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

  goToProfile() {
    console.log(`/store/${this.userStore.storeName}`);
    this.router.navigate([`/store/${this.userStore.storeName}`]);
  }

  goToRecomendations() {
    this.router.navigate([`/recomendations`]);
  }
}
