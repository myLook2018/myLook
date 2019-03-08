import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-promote-dialog',
  templateUrl: 'promoteDialog.html',
})
export class PromoteDialogComponent {
  maxDate:any;
  onAdd = new EventEmitter();
  dailyCost;
  promotionCost;
  diferenceInDays;
  minDate = new Date();
  dueDate;
  selectedPromotion;
  selectedPayMethod: Number;
  promotionData;

  promotionsLevels = [
    { value: 1, viewValue: 'Sin Promoción' },
    { value: 2, viewValue: 'Promoción Básica' },
    { value: 3, viewValue: 'Promoción Premium' }
  ];

  payMethods = [
    { value: 0, viewValue: 'Gratis!' }
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
      promotionCost: this.promotionCost,
    };
    this.onAdd.emit(this.promotionData);
  }

  tryCalculateCost() {
    try {
      const diff = Math.abs(this.dueDate.getTime() - new Date().getTime());
      this.diferenceInDays = Math.ceil(diff / (1000 * 3600 * 24));
      console.log(`diference in days ` + this.diferenceInDays);
      console.log(`promotion ` + this.selectedPromotion);
      this.dailyCost  = (this.selectedPromotion - 1 ) * 5;
      this.promotionCost = this.diferenceInDays * (this.selectedPromotion - 1 ) * 5;
      console.log(`dailyCost ` + this.dailyCost);
    } catch {
      console.log(`no pude calcular diff`);
    }
  }
}
