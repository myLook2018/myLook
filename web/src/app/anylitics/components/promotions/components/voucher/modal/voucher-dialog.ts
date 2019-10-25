import { Component, Inject, EventEmitter } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatHorizontalStepper } from '@angular/material';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from 'src/app/articles/services/article.service';
import { PreferenceMP } from 'src/app/articles/models/preferenceMP';

@Component({
  selector: 'app-voucher-dialog',
  templateUrl: 'voucher-dialog.html',
  styleUrls: ['./voucher-dialog.scss'],
})
export class VoucherDialogComponent {
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
  baseCost: number;
  userData;
  isDisabled = true;
  voucherType = 0;
  voucherValue = 5;
  genderSelected = '';


  promotionsLevels = [
    { value: 0, viewValue: 'Campaña Basica' },
    { value: 1, viewValue: 'Campaña Premium' }
  ];

  voucherTypes = [
    { value: 0, viewValue: '2 x 1' },
    { value: 1, viewValue: '3 x 2' },
    { value: 2, viewValue: 'Porcentaje de Descuento' },
  ];

  genderTypes = [
    { value: 'male', viewValue: 'Masculino'},
    { value: 'female', viewValue: 'Femenino'},
    { value: 'otro', viewValue: 'Otro'},
    { value: 'both', viewValue: 'Indistinto'},
  ];

  durationOptions = [
    { value: 2, viewValue: '2 días'},
    { value: 3, viewValue: '3 días'},
    { value: 4, viewValue: '4 días'},
    { value: 5, viewValue: '5 días'},
    { value: 6, viewValue: '6 días'},
    { value: 7, viewValue: '7 días'},
    { value: 8, viewValue: '8 días'},
    { value: 9, viewValue: '9 días'},
    { value: 10, viewValue: '10 días'},
    { value: 11, viewValue: '11 días'},
    { value: 12, viewValue: '12 días'},
    { value: 13, viewValue: '13 días'},
    { value: 14, viewValue: '14 días'},
    { value: 15, viewValue: '15 días'},
    { value: 16, viewValue: '16 días'},
    { value: 17, viewValue: '17 días'},
    { value: 18, viewValue: '18 días'},
    { value: 19, viewValue: '19 días'},
    { value: 20, viewValue: '20 días'},
    { value: 21, viewValue: '21 día'},
    { value: 28, viewValue: '1 mes'},
  ]

  payMethods = [{ value: 0, viewValue: 'Gratis!' }];
  isLoading = false;
  disableButton = false;
  discountNumber: FormControl;
  fromAge: FormControl;
  toAge: FormControl;
  isAgeCorrect = true;
  extraClients = 0;
  sliderValue = 0;
  maxSlider = 40;
  minSlider = 0;
  constructor(
    public dialogRef: MatDialogRef<VoucherDialogComponent>,
    private _formBuilder: FormBuilder,
    private articleService: ArticleService,
    @Inject(MAT_DIALOG_DATA) public data
    ) {
      this.firstFormGroup = this._formBuilder.group({
        promotionLevelCtrl: ['', Validators.required],
        durationCtrl: ['', Validators.required]

      });
      this.userData = data;
      this.discountNumber = new FormControl(0, [Validators.max(100), Validators.min(5)]);
      this.fromAge = new FormControl(0, [Validators.max(100), Validators.min(0)]);
      this.toAge = new FormControl(0, [Validators.max(100), Validators.min(this.fromAge.value)]);
    }


  onNoClick(): void {
    this.dialogRef.close();
    console.log('me cancelaron');
  }

  initializePreference() {
    console.log('lo que tenemos de dato', this.userData);
    this.preferenceMP =  {
      'items': [{
        'id': `${this.selectedPromotion}-${this.duration}`,
        'title': 'Campaña de Cupones - MyLook',
        'quantity': 1,
        'currency_id': 'ARS',
        'unit_price': this.promotionCost
        }],
      'payer': {
          'name': this.userData.storeName,
          'surname': this.userData.storeName,
          'email': this.userData.email,
          'phone': {
              'area_code': this.userData.phoneArea,
              // tslint:disable-next-line: radix
              'number': parseInt(this.userData.phone)
          },
          'identification': {
              'type': 'DNI', // Available ID types at https://api.mercadopago.com/v1/identification_types
              'number': this.userData.dni
          }
      },
      'back_urls': {
        'success': `https://app-mylook.firebaseapp.com/Tiendas${this.userData.storeName}/Promociones`,
        'failure': 'https://app-mylook.firebaseapp.com/Tiendas/Error',
    },
    'auto_return': 'approved',
    'external_reference': `${this.selectedPromotion}-${this.duration}`
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
    // if (this.selectedPromotion) {
      this.baseCost = 50;
    // } else { this.baseCost = 0; }
      console.log('slider value ', this.sliderValue)
      this.promotionCost = this.baseCost + ( this.sliderValue * 0.20);

    try {
      console.log(`Promocion por ${this.duration} dias.`);
      console.log(`Nivel de promocion: ${this.selectedPromotion}.`);
      console.log('a pagar ' + this.promotionCost );

    } catch {
      console.log(`no pude calcular diff`);
    }
  }

  trySendDatsToMP(stepper: MatHorizontalStepper) {
          stepper.next();
  }

  async sendToMP() {
    const shouldAgeBeLoaded = this.selectedPromotion === 1;
    const isAgeBad = this.fromAge.value >= this.toAge.value;

    if (shouldAgeBeLoaded && isAgeBad) {
      this.isAgeCorrect = false;
      return;
    }
    this.initializePreference();
    console.log('mandando al servicio', this.preferenceMP);
    this.isLoading = true;
    this.disableButton = true;
    const res: any = await this.articleService.tryPromoteMP(this.preferenceMP).toPromise();
    window.open(res.initPoint, '_self');
  }
}
