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
import { FirebaseUserModel } from '../../../auth/models/user.model';
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
  user = new FirebaseUserModel();
  articles: Article[];
  _subscription: Subscription;
  _subscription2: Subscription;
  constructor(
    fb: FormBuilder,
    public articleService: ArticleService,
    public dialog: MatDialog,
    public userService: UserService,
    public authService: AuthService,
    private location: Location,
    private router: Router,
    private spinner: NgxSpinnerService,
    private route: ActivatedRoute,
    ) {
      this.options = fb.group({
        hideRequired: false,
        floatLabel: 'never'
      });
      this.user.image = '/assets/alternativeUserPic.png';
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
        console.log('estoy ya en el inventario');
        const data = routeData['data'];
            if (data) {
              this.user = data as FirebaseUserModel;
          }
      });

      this._subscription2 = this.userService.getUserInfo(this.user.userId).subscribe(userA => {
        console.log(userA);
        console.log(userA[0].userPhone);
        this.user.userName = userA[0].userName;
        console.log('nombre para comparar: ' + userA[0].userName);
        this.user.userPhone = userA[0].userPhone;
        this._subscription = this.articleService.getArticles(this.user.userName).subscribe(articles => {
          console.log(articles);
          this.articles = articles;
          console.log(this.articles.length);
          this.dataSource = new MatTableDataSource(this.articles);
          setTimeout(() => {
            /** spinner ends after  seconds */
            this.spinner.hide();
          }, 2000);
        }
        );
      }
        );
    }

  ngOnDestroy(): void {
    console.log('no me destruyo la concha de la lora');
    this._subscription.unsubscribe();
    this._subscription2.unsubscribe();
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
        storeName: this.user.userName,
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
        dataToSend = { storeName: this.user.userName };
        }

    const dialogRef = this.dialog.open(ArticleDialogComponent, {
      height: '630px',
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

  goToProfile() {
    console.log(`/store/${this.user.userName}`);
    this.router.navigate([`/store/${this.user.userName}`]);
  }
}
