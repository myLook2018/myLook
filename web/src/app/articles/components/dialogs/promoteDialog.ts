import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-promote-dialog',
  templateUrl: 'promoteDialog.html',
})
export class PromoteDialogComponent {
  onAdd = new EventEmitter();
  dailyCost;
  minDate = new Date();
  dueDate;
  selectedPromotion: Number;
  selectedPayMethod: Number;
  promotionData;

  promotionsLevels = [
    { value: 1, viewValue: 'Sin Promoción' },
    { value: 2, viewValue: 'Promoción Básica' },
    { value: 3, viewValue: 'Promoción Premium' }
  ];

  payMethods = [
    { value: 0, viewValue: 'Gratis!'}
  ];
  constructor(
    public dialogRef: MatDialogRef<PromoteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  sendData() {
    this.promotionData = {
      dueDate: this.dueDate,
      promotionLevel: this.selectedPromotion,
      payMethod: this.selectedPayMethod,
      dailyCost: this.dailyCost,
    };
    this.onAdd.emit(this.promotionData);
  }
}
