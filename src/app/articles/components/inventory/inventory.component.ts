import { Component } from '@angular/core';
import { Article } from './../../models/article';
import { FormGroup, FormBuilder } from '../../../../../node_modules/@angular/forms';

const ELEMENT_DATA: Article[] = [
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , },
  {id: 'asd', picture: '', cost: 300, size: 'L', material: 'Hilo', colors: 'red',
   initial_stock: 5, provider: 'pepe', tags: ['a', 'b'] , }
];


@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss']
})



export class InventoryComponent {
  options: FormGroup;

  constructor(fb: FormBuilder) {
    this.options = fb.group({
      hideRequired: false,
      floatLabel: 'never',
    });
  }

  displayedColumns: string[] = ['picture', 'cost', 'size', 'material' , 'colors', 'initial_stock', 'tags'];
  dataSource = ELEMENT_DATA;
}

