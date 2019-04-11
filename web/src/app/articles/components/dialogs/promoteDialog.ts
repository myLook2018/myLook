import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-promote-dialog',
  templateUrl: 'promoteDialog.html'
})
export class PromoteDialogComponent {
  mercadopago: any;
  maxDate: any;
  onAdd = new EventEmitter();
  dailyCost;
  promotionCost;
  diferenceInDays;
  minDate = new Date();
  duration;
  selectedPromotion;
  selectedPayMethod: Number;
  promotionData;

  promotionsLevels = [
    { value: 1, viewValue: 'Sin Promoción' },
    { value: 2, viewValue: 'Promoción Básica' },
    { value: 3, viewValue: 'Promoción Premium' }
  ];

  durations = [
    { value: 1, viewValue: '1 día de promoción' },
    { value: 2, viewValue: '2 dias de promoción' },
    { value: 3, viewValue: '3 dias de promoción' },
    { value: 5, viewValue: '5 dias de promoción' },
    { value: 7, viewValue: '1 semana de promoción' },
    { value: 14, viewValue: '2 semana de promoción' },
    { value: 28, viewValue: '1 mes de promoción' }
  ];

  payMethods = [{ value: 0, viewValue: 'Gratis!' }];
  constructor(
    public dialogRef: MatDialogRef<PromoteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  sendData() {
    this.promotionData = {
      duration: this.duration,
      promotionLevel: this.selectedPromotion,
      payMethod: this.selectedPayMethod,
      promotionCost: this.promotionCost
    };
    this.onAdd.emit(this.promotionData);
  }

  tryCalculateCost() {
    const finalCost = 10 * this.duration * (this.selectedPromotion - 1);

    switch (this.duration) {
      case 1: {
        this.promotionCost = finalCost;
        break;
      }
      case 2: {
        this.promotionCost = finalCost * 0.95;
        break;
      }
      case 3: {
        this.promotionCost = finalCost * 0.9;
        break;
      }
      case 5: {
        this.promotionCost = finalCost * 0.85;
        break;
      }
      case 7: {
        this.promotionCost = finalCost * 0.80;
        break;
      }
      case 14: {
        this.promotionCost = finalCost * 0.75;
        break;
      }
      case 14: {
        this.promotionCost = finalCost * 0.70;
        break;
      }
    }

    try {
      console.log(`Promocion por ${this.duration} dias.`);
      console.log(`Nivel de promocion: ${this.selectedPromotion}.`);
    } catch {
      console.log(`no pude calcular diff`);
    }
  }
}
