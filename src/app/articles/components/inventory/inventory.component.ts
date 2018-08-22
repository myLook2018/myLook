import { Component, ViewChild, OnInit } from '@angular/core';
import { Article } from '../../models/article';
import { FormGroup, FormBuilder } from '@angular/forms';
import { UpLoadArticleComponent } from '../dialogs/uploadArticle';
import { MatDialog, MatTableDataSource, MatSort } from '@angular/material';
import { AuthService } from '../../../auth/services/auth.service';
import { Location } from '@angular/common';

const ELEMENT_DATA: Article[] = [
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum9',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  },
  {
    id: 'Lorem ipsum',
    picture: '',
    cost: 5,
    size: 'Lorem ipsum',
    material: 'Lorem ipsum',
    colors: 'Lorem ipsum',
    initial_stock: 5,
    provider: 'Lorem ipsum',
    tags: ['Lorem', 'ipsum']
  }
];
@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})


export class InventoryComponent implements OnInit {
  options: FormGroup;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    fb: FormBuilder,
    public dialog: MatDialog,
    public authService: AuthService,
    private location: Location
  ) {
    this.options = fb.group({
      hideRequired: false,
      floatLabel: 'never'
    });
  }

  displayedColumns: string[] = [
    'picture',
    'cost',
    'size',
    'material',
    'colors',
    'initial_stock',
    'tags'
  ];

  dataSource = new MatTableDataSource(ELEMENT_DATA);
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }


    ngOnInit() {
      this.dataSource.sort = this.sort;
    }

  openDialog(): void {
    const dialogRef = this.dialog.open(UpLoadArticleComponent, {
      height: '600px',
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
      }
    );
  }
}
