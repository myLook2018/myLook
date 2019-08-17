import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatHorizontalStepper } from '@angular/material';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ArticleService } from './../../services/article.service';
import { PreferenceMP } from './../../models/preferenceMP';

@Component({
  selector: 'app-promote-dialog',
  templateUrl: 'promoteDialog.html',
  styleUrls: ['./promoteDialog.scss'],
})
export class PromoteDialogComponent {
  step1: any;

  urlMercadoPago = 'la pinche url';
  maxDate: any;
  onAdd = new EventEmitter();
  dailyCost;
  promotionCost = 0;
  diferenceInDays;
  minDate = new Date();
  duration = 0;
  selectedPromotion;
  selectedPayMethod: Number;
  promotionData;
  firstFormGroup: FormGroup ;
  preferenceMP: PreferenceMP;
  finalCost: number;
  userData;
  isDisabled = true;

  promotionsLevels = [
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
  isLoading = false;
  disableButton = false;
  constructor(
    public dialogRef: MatDialogRef<PromoteDialogComponent>,
    private _formBuilder: FormBuilder,
    private articleService: ArticleService,
    @Inject(MAT_DIALOG_DATA) public data
    ) {
      this.firstFormGroup = this._formBuilder.group({
        promotionLevelCtrl: ['', Validators.required],
        durationCtrl: ['', Validators.required]

      });
      this.userData = data;

    }


  onNoClick(): void {
    this.dialogRef.close();
    console.log('me cancelaron')
  }

  initializePreference() {
    console.log('lo que tenemos de dato', this.userData);
    this.preferenceMP =  {
      'items': [{
        'id': `${this.selectedPromotion}-${this.duration}-${this.userData.articleId}`,
        'title': 'Promoción de prenda - MyLook',
        'quantity': 1,
        'currency_id': 'ARS',
        'picture_url': this.userData.picturesArray[0],
        'unit_price': this.promotionCost
        }],
      'payer': {
          'name': this.userData.storeName,
          'surname': this.userData.storeName,
          'email': this.userData.email,
          'phone': {
              'area_code': this.userData.phoneArea,
              'number': this.userData.phone
          },
          'identification': {
              'type': 'DNI', // Available ID types at https://api.mercadopago.com/v1/identification_types
              'number': this.userData.dni
          }
      },
      'back_urls': {
        'success': `https://app-mylook.firebaseapp.com/Tiendas${this.userData.storeName}/Catalogo`,
        'failure': 'https://app-mylook.firebaseapp.com/Tiendas/Error',
    },
    'auto_return': 'approved',
    'external_reference': `${this.selectedPromotion}-${this.duration}-${this.userData.articleId}`
    };
  }

  sendData() {
    this.promotionData = {
      startOfPromotion: new Date(),
      duration: this.duration,
      promotionLevel: this.selectedPromotion,
      payMethod: 0,
      promotionCost: this.promotionCost,
    };
    console.log('lad data que sale del dialog', this.promotionData);
    this.onAdd.emit(this.promotionData);
  }

  tryCalculateCost() {
    if (this.selectedPromotion) {
      this.finalCost = 10 * this.duration * (this.selectedPromotion - 1);
    } else { this.finalCost = 0; }

    switch (this.duration) {
      case 1: {
        this.promotionCost = this.finalCost;
        break;
      }
      case 2: {
        this.promotionCost = this.finalCost * 0.95;
        break;
      }
      case 3: {
        this.promotionCost = this.finalCost * 0.9;
        break;
      }
      case 5: {
        this.promotionCost = this.finalCost * 0.85;
        break;
      }
      case 7: {
        this.promotionCost = this.finalCost * 0.80;
        break;
      }
      case 14: {
        this.promotionCost = this.finalCost * 0.75;
        break;
      }
      case 14: {
        this.promotionCost = this.finalCost * 0.70;
        break;
      }
      case undefined: {
        this.promotionCost = 0;
      }
    }

    try {
      console.log(`Promocion por ${this.duration} dias.`);
      console.log(`Nivel de promocion: ${this.selectedPromotion}.`);
      console.log('a pagar ' + this.finalCost );

    } catch {
      console.log(`no pude calcular diff`);
    }
  }

  trySendDatsToMP(stepper: MatHorizontalStepper) {
          stepper.next();
  }

  async sendToMP() {
    this.initializePreference();
    console.log('mandando al servicio', this.preferenceMP);
    this.isLoading = true;
    this.disableButton = true;
    const res: any = await this.articleService.tryPromoteMP(this.preferenceMP).toPromise();
    window.open(res.initPoint, '_self');
  }
}
