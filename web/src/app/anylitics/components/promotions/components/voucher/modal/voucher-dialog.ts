import { Component, Inject, EventEmitter, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatHorizontalStepper } from '@angular/material';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { ArticleService } from 'src/app/articles/services/article.service';
import { PreferenceMP } from 'src/app/articles/models/preferenceMP';
import { DataService } from 'src/app/service/dataService';

@Component({
  selector: 'app-voucher-dialog',
  templateUrl: 'voucher-dialog.html',
  styleUrls: ['./voucher-dialog.scss'],
})
export class VoucherDialogComponent implements OnInit {
  step1: any;

  urlMercadoPago = 'la pinche url';
  maxDate: any;
  onAdd = new EventEmitter();
  dailyCost;
  promotionCost = 0;
  diferenceInDays;
  minDate = new Date();
  duration = 0;
  selectedCampaing;
  selectedPayMethod: Number;
  promotionData;
  firstFormGroup: FormGroup ;
  preferenceMP: PreferenceMP;
  baseCost: number;
  userData;
  isDisabled = true;
  voucherType = -1;
  voucherValue = 0;
  genderSelected = '';


  campaignTypes = [
    { value: 0, viewValue: 'Campaña Basica' },
    { value: 1, viewValue: 'Campaña Premium' }
  ];

  voucherTypes = [
    { value: 0, viewValue: '2 x 1' },
    { value: 1, viewValue: 'Porcentaje de Descuento' },
  ];

  genderTypes = [
    { value: 'Masculino', viewValue: 'Masculino'},
    { value: 'Femenino', viewValue: 'Femenino'},
    { value: 'Otro', viewValue: 'Otro'},
    { value: 'Todos', viewValue: 'Todos'},
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
    { value: 21, viewValue: '21 días'},
    { value: 28, viewValue: '1 mes'},
  ];

  payMethods = [{ value: 0, viewValue: 'Gratis!' }];
  isLoading = false;
  disableButton = false;
  discountNumber: FormControl;
  fromAge: FormControl;
  toAge: FormControl;
  isAgeCorrect = true;
  extraClients = [];
  sliderValue = 0;
  maxSlider = 0;
  minSlider = 0;
  description: FormControl;
  clientsTotal: Array<any>;
  subscritorsTotal: Array<any>;
  allNonSubscribers: Array<any>;
  filteredNonSubscribers: any[];
  voucherReference: string;
  title: FormControl;
  constructor(
    public dialogRef: MatDialogRef<VoucherDialogComponent>,
    private _formBuilder: FormBuilder,
    private articleService: ArticleService,
    private dataService: DataService,

    @Inject(MAT_DIALOG_DATA) public data
    ) {
      this.firstFormGroup = this._formBuilder.group({
        promotionLevelCtrl: ['', Validators.required],
        durationCtrl: ['', Validators.required]

      });
      this.userData = data;
      this.discountNumber = new FormControl(0, [Validators.max(100), Validators.min(0)]);
      this.fromAge = new FormControl(0, [Validators.max(100), Validators.min(0)]);
      this.toAge = new FormControl(0, [Validators.max(100), Validators.min(0)]);
      this.description = new FormControl('');
      this.title = new FormControl('');
      this.dataService.getNumberClients().then( clients => {
         this.clientsTotal = clients;
         console.log('clientes ', this.clientsTotal);
         this.dataService.getNumberOfSubscriptors().then( subscriptos => {
           this.subscritorsTotal = subscriptos;
           console.log('estos son mis subs', this.subscritorsTotal);
           console.log('estos son mis subs lengt', this.subscritorsTotal.length);
           this.addClientIDtoSubscriptors();
           this.allNonSubscribers = this.clientsTotal.filter(client => !this.subscritorsTotal.find(subs => {
             console.log(`${subs.userId} === ${client.userId}`);
             // tslint:disable-next-line: triple-equals
             console.log(subs.userId == client.userId);
             return subs.userId === client.userId;
            }));
           console.log('los no subscriptos', this.allNonSubscribers);
           console.log('slider va a tener max', this.maxSlider);
          });
        });
    }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
    console.log('me cancelaron');
  }

  initializePreference() {
    console.log('lo que tenemos de dato', this.data);
    this.preferenceMP =  {
      'items': [{
        // tslint:disable-next-line: max-line-length
        'id': this.voucherReference,
        'title': 'Campaña de Cupones - MyLook',
        'quantity': 1,
        'currency_id': 'ARS',
        'unit_price': 1,
        // 'unit_price': this.promotionCost,
        }],
      'payer': {
          'name': this.data.storeName,
          'surname': this.data.storeName,
          'email': this.data.email,
          'phone': {
              'area_code': this.data.phoneArea,
              // tslint:disable-next-line: radix
              'number': parseInt(this.data.phone)
          },
          'identification': {
              'type': 'DNI', // Available ID types at https://api.mercadopago.com/v1/identification_types
              'number': this.data.dni
          }
      },
      'back_urls': {
        'success': `https://app-mylook.firebaseapp.com/Tiendas/${this.data.storeName}/Promociones`,
        'failure': 'https://app-mylook.firebaseapp.com/Tiendas/Error',
    },
    'auto_return': 'approved',
    'external_reference': this.voucherReference
    };
  }

  sendData() {
    this.promotionData = {
      startOfPromotion: new Date(),
      duration: this.duration,
      promotionLevel: this.selectedCampaing,
      payMethod: 0,
      promotionCost: this.promotionCost,
    };
    console.log('lad data que sale del dialog', this.promotionData);
    this.onAdd.emit(this.promotionData);
  }

  tryCalculateCost() {
      if (this.voucherType === 0 ) {this.discountNumber.setValue(0); }
      this.baseCost = 50;
      console.log('slider value ', this.sliderValue);
      this.promotionCost = this.baseCost + ( this.sliderValue * 0.20);

    try {
      console.log(`Promocion por ${this.duration} dias.`);
      console.log(`Nivel de selectedCampaing: ${this.selectedCampaing}.`);
      console.log('a pagar ' + this.promotionCost );
      this.selectRandomExtraClients();

    } catch {
      console.log(`no pude calcular diff`);
    }
  }

  selectRandomExtraClients() {
    for (let index = 0; index < this.sliderValue; index++) {
      const randomNumber = Math.floor(Math.random() * this.filteredNonSubscribers.length);
      const randomClient = this.filteredNonSubscribers[randomNumber].id;
      this.filteredNonSubscribers.splice(randomNumber, 1);
      this.extraClients.push(randomClient);
    }
  }

  trySendDatsToMP(stepper: MatHorizontalStepper) {
          stepper.next();
  }

  async sendToMP() {
    const shouldAgeBeLoaded = this.selectedCampaing === 1;
    const isAgeBad = this.fromAge.value >= this.toAge.value;

    if (shouldAgeBeLoaded && isAgeBad) {
      this.isAgeCorrect = false;
      return;
    }
    this.generateDocumentNotPaid().then((data) => {
      console.log('lo que creamos', data);
      this.voucherReference = data.id;
      this.initializePreference();
      console.log('mandando al servicio', this.preferenceMP);
      this.isLoading = true;
      this.disableButton = true;
      this.articleService.tryPromoteMP(this.preferenceMP).toPromise().then( (res: any) => {
        window.open(res.initPoint, '_self');
      });
    });
  }

  async generateDocumentNotPaid() {
    const discount = this.voucherType === 0 ? null : this.discountNumber.value;
    const end = new Date;
    const subsIds = [];
    this.subscritorsTotal.forEach(subscriptor => {
      subsIds.push(subscriptor.idClientDocument);
    });
    end.setDate(end.getDate() + this.duration);
    const documentData = {
      startDate: new Date,
      dueDate: end,
      title: this.title.value,
      storeId: this.data.storeId,
      storeName: this.data.storeName,
      payMethod: null,
      voucherType: this.voucherType,
      campaignType: this.selectedCampaing,
      discountValue: discount,
      campaignCost: this.promotionCost,
      idMercadoPago: null,
      paymentMethod: null,
      lastFourDigits: null,
      cardOwner: null,
      description: this.description.value,
      genderFocus: this.genderSelected,
      fromAge: this.fromAge.value,
      toAge: this.toAge.value,
      clientsId: subsIds.concat(this.extraClients)
    };

    console.log('data a crear del voucher', documentData);
    return this.dataService.addNewVoucherCollection(documentData);
  }

  recalculateExtraClients() {
    console.log('-+'.repeat(15));
    console.log('recalculando clientes extras');
    this.filteredNonSubscribers = this.allNonSubscribers.filter(nonSub => {
      // match del genero
      let isGender = true;
      if (this.genderSelected !== 'Todos') {
        isGender = nonSub.gender === this.genderSelected;
      }

      // match edad
      let isBetweenAges = true;
      const dateBirthday = new Date(nonSub.birthday);
      console.log('fecha de nacimiento?', dateBirthday);
      const ageDifMs = Date.now() - dateBirthday.getTime();
      const ageDate = new Date(ageDifMs); // miliseconds from epoch
      const realAge = Math.abs(ageDate.getUTCFullYear() - 1970);

      // tslint:disable-next-line: radix
      const from = parseInt(this.fromAge.value);
      // tslint:disable-next-line: radix
      const to = parseInt(this.toAge.value);
      isBetweenAges = ( from  < realAge && realAge < to );
      console.log(`${from} < ${realAge} < ${to}`);
      console.log('isBetweenAges', isBetweenAges);
      console.log('isGender', isGender);

      return (isGender && isBetweenAges);
    });
    console.log('encontramos de filtrar a ' , this.filteredNonSubscribers.length);
    this.maxSlider = this.filteredNonSubscribers.length;
  }

  addClientIDtoSubscriptors() {
    this.subscritorsTotal.map( subs => {
      console.log('agregandole clientID a ', subs)
      const clientDoc = this.clientsTotal.find( client => client.userId === subs.userId );
      subs.idClientDocument = clientDoc.id;
      console.log('Ahora quedo ', subs);
    });
  }
}
