import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { VoucherDialogComponent } from './modal/voucher-dialog';
import { MatDialog, MatTableDataSource } from '@angular/material';
import { AnyliticService } from 'src/app/anylitics/services/anylitics.service';
import { PromotionsService } from 'src/app/anylitics/services/promotions.service';
import { ModalCheckComponent } from './modal-check/modal-check.component';
import { DataService } from 'src/app/service/dataService';

@Component({
  selector: 'app-voucher',
  templateUrl: './voucher.component.html',
  styleUrls: ['./voucher.component.scss']
})
export class VoucherComponent implements OnInit, OnChanges {
  userStore: any;

  voucherCampaigns = [];
  campaignsTableDataSource: MatTableDataSource<unknown>;
  displayedColumns = ['Titulo', 'FechaInicio', 'FechaFin', 'TipoCampania', 'PrecioFinal', 'Descargar'];

  vouchersTotal: any;
  selectedCampaing: any;
  mySubscriptors = [];
  clientsTotal = [];
  nonSubscribersOfSelectedCampaing: any[];

  usedCount = 0;
  nonUsedCount = 0;
  nonSubSuccessTooltip = 'Muestra la cantidad de usuarios no susscriptos a los que le llegó tu cupón.';

  lessThan18 = 0;
  from18to21 = 0;
  from22to25 = 0;
  from26to29 = 0;
  moreThan30 = 0;
  byAgeTooltip = 'Muestra el rango de edad de aquellos usuarios que utilizaron tus cupones.';

  maleCounter = 0;
  femaleCounter = 0;
  otherGenderCounter = 0;
  genderCounter = 'Muestra el genero de aquellos usuarios que utilizaron tus cupones.';

  InteractionsXday: any[];
  interactionsByDay: any[];
  visitsByDay: any[];
  reactionsByDay: any[];
  favoriteByDay: any[];
  readyToRender: boolean;
  noDats: boolean;
  totalCost = 0;
  graphsLoaded = false;

  daysOfTheWeek = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
  campaingUsedByDay = [ 0, 0, 0, 0, 0, 0, 0];
  globalUsedByDay = [ 0, 0, 0, 0, 0, 0, 0];
  linesTooltip = 'Muestra los dias en los que los cupones son utilizados.';
  disableRefreshButton = true;
  constructor(
    public dialog: MatDialog,
    private analyticsService: AnyliticService,
    private promotionsService: PromotionsService,
    private dataService: DataService
  ) {}

  ngOnInit() {
    this.dataService.getStoreInfo().then(store => {
      this.userStore = store;
      console.log('actual store', this.userStore);
      this.analyticsService.getVoucherCampaings(this.userStore.firebaseUID).then( campaings => {
        this.voucherCampaigns = campaings;
        console.log('estas son las campañas para esta tienda ', this.voucherCampaigns);
        this.campaignsTableDataSource = new MatTableDataSource(this.voucherCampaigns);
        this.analyticsService.getAllVouchersForStore(this.userStore.firebaseUID).then( vouchers => {
          console.log('estos son los vouchers de la tienda', vouchers);
          this.vouchersTotal = vouchers;
          this.dataService.getNumberClients().then( clients => {
            this.clientsTotal = clients;
            this.dataService.getNumberOfSubscriptors().then( subscriptos => {
              this.mySubscriptors = subscriptos;
              this.addClientIDtoSubscriptors();
              this.asociateVouchersToCampaing();
              this.selectedCampaing = this.voucherCampaigns[0];
              this.getGraphsInformation();
              this.totalCost = this.getTotalCost();
              this.getGlobalLinesByDayInfo();
              this.disableRefreshButton = false;
            });
          });
        });
        if (this.voucherCampaigns.length === 0) {
          this.noDats = true;
        }
      });

    });
  }

  ngOnChanges() {
  }

  openNewVoucher() {
    const phoneAux = this.userStore.phone ? this.userStore.phone : 123456789;
    const dataToSend = {
      storeId: this.userStore.firebaseUID,
      storeName: this.userStore.storeName,
      phone: phoneAux,
      phoneArea: '2966',
      ownerName: this.userStore.ownerName,
      storeEmail: this.userStore.storeMail,
      dni: '38773582',
    };
    const promoteRef = this.dialog.open(VoucherDialogComponent, {
      width: '950px',
      disableClose: true,
      data: dataToSend
    });
    const sub = promoteRef.componentInstance.onAdd.subscribe(res => {
      if (res !== undefined) {
        console.log('salio todo ben');
        // this.promoteArticle(res, article, this.userStore.firebaseUID);
        // this.RedirectToMercadoPago(res);
      }
    });
    promoteRef.afterClosed().subscribe(result => {
      console.log(`resutl close ` + result);
      sub.unsubscribe();
    });
  }

  openCheckVoucher() {
    const dataToSend = {
      storeId: this.userStore.firebaseUID,
      storeName: this.userStore.storeName,
    };
    const promoteRef = this.dialog.open(ModalCheckComponent, {
      width: '600px',
      disableClose: true,
      data: dataToSend
    });

    promoteRef.afterClosed().subscribe(result => {
      console.log(`resutl close ` + result);
    });
  }

  // Comportamiento para la tabla de descarga

  getCampaignType( type ) {
    return type === 0 ? 'Campaña Estandar' : 'Campaña Premium';
  }

  getTotalCost() {
    let parcialTotalCost = 0;
    this.voucherCampaigns.forEach( campaing => { parcialTotalCost = parcialTotalCost + campaing.campaignCost; });
    return parcialTotalCost;
  }

  downloadCampaign( element: any ) {
    console.log(element);
    const data = {info: element, store: this.userStore };
    this.promotionsService.downloadDocument(data, 'campaigns');
  }

  getGraphsInformation() {
    this.resetCounters();
    this.getNonSubsSuccessInfo();
    this.getBarByAgeInfo();
    this.getPieByGender();
    this.getLinesByDayInfo();
    this.readyToRender = true;

    setTimeout(() => {
      this.graphsLoaded = true;
      this.disableRefreshButton = false;
    }, 500);
  }

  resetCounters() {
    this.usedCount = 0;
    this.nonUsedCount = 0;

    this.lessThan18 = 0;
    this.from18to21 = 0;
    this.from22to25 = 0;
    this.from26to29 = 0;
    this.moreThan30 = 0;

    this.maleCounter = 0;
    this.femaleCounter = 0;
    this.otherGenderCounter = 0;
  }

  asociateVouchersToCampaing() {
    this.voucherCampaigns.forEach( campaign => {
      this.vouchersTotal.forEach( voucher => {
        console.log(voucher.campaignId === campaign.id);
        if ( voucher.campaignId === campaign.id ) {
          campaign.vouchers.push(voucher); }
      });
    });
  }

  getNonSubsSuccessInfo() {
    this.nonSubscribersOfSelectedCampaing = [];
    if ( this.selectedCampaing ) {
      this.selectedCampaing.vouchers.forEach( voucher => {
        console.log('my subs', this.mySubscriptors);
        if (!this.mySubscriptors.find( subscriptor =>  voucher.clientId === subscriptor.idClientDocument )) {
          this.nonSubscribersOfSelectedCampaing.push(voucher);
        }
      });
      // ahora tengo los vouchers de los no subscriptos
      this.nonSubscribersOfSelectedCampaing.forEach( voucher => {
        if (voucher.used) {
          this.usedCount++;
        } else {
          this.nonUsedCount++;
        }
      });
    }
  }

  addClientIDtoSubscriptors() {
    this.mySubscriptors.map( subs => {
      const clientDoc = this.clientsTotal.find( client => client.userId === subs.userId );
      if (clientDoc) {
        subs.idClientDocument = clientDoc.id;
      }
    });
  }

  getBarByAgeInfo() {
    if ( this.selectedCampaing) {

      this.selectedCampaing.vouchers.forEach( voucher => {
      if (voucher.used) {
        switch (true) {
          case (voucher.age < 18):
            this.lessThan18++;
            break;
            case (voucher.age >= 18 && voucher.age <= 21 ):
              this.from18to21++;
              break;
              case (voucher.age >= 22 && voucher.age <= 25 ):
          this.from22to25++;
          break;
          case (voucher.age >= 26 && voucher.age <= 29 ):
            this.from26to29++;
            break;
            case (voucher.age >= 30 ):
              this.moreThan30++;
              break;
              default:
                break;
              }
            }
          });
        }
  }

  getPieByGender() {
    if ( this.selectedCampaing) {
      this.selectedCampaing.vouchers.forEach( voucher => {
        if ( voucher.used) {
          switch (true) {
            case (voucher.gender === 'Masculino'):
              this.maleCounter++;
              break;
            case (voucher.gender === 'Femenino'):
              this.femaleCounter++;
              break;
            case (voucher.gender === 'Otro'):
              this.otherGenderCounter++;
              break;
            default:
              console.log('encontramos un voucher con genero rara: ', voucher);
              break;
          }
        }
      });
    }
    console.log('gender male ', this.maleCounter);
    console.log('gender female ', this.femaleCounter);
    console.log('gender other ', this.otherGenderCounter);
  }

  getLinesByDayInfo() {
    this.campaingUsedByDay = [ 0, 0, 0, 0, 0, 0, 0];

    if (this.selectedCampaing) {

      this.selectedCampaing.vouchers.forEach((voucher) => {
        if (voucher.usedDate !== null) {
          const dayOfUsed = voucher.usedDate.toDate().getDay();
          this.campaingUsedByDay[dayOfUsed]++;
        }
      });
      console.log( 'campaing used', this.campaingUsedByDay);
    }
  }

  getGlobalLinesByDayInfo() {
    this.vouchersTotal.forEach((voucher) => {
      if (voucher.usedDate !== null) {
        const dayOfUsed = voucher.usedDate.toDate().getDay();

        this.globalUsedByDay[dayOfUsed]++;
      }
    });

    console.log('global' , this.globalUsedByDay);
  }

  campaingSelected( campaña ) {
    this.disableRefreshButton = true;
    console.log('event ', campaña);
    this.selectedCampaing = campaña.value;
    console.log('se selecciono esta campaña ', this.selectedCampaing);
    this.graphsLoaded = false;
    this.getGraphsInformation();
  }

  refreshVouchers() {
    console.log('refrescando');
    this.disableRefreshButton = true;
    this.graphsLoaded = false;
    this.analyticsService.getAllVouchersForStore(this.userStore.firebaseUID).then( vouchers => {
      console.log('estos son los vouchers de la tienda', vouchers);
      this.vouchersTotal = vouchers;
      this.dataService.getNumberClients().then( clients => {
        this.clientsTotal = clients;
        this.dataService.getNumberOfSubscriptors().then( subscriptos => {
          this.mySubscriptors = subscriptos;
          this.addClientIDtoSubscriptors();
          this.asociateVouchersToCampaing();
          // this.selectedCampaing = this.voucherCampaigns[0];
          this.getGraphsInformation();
          this.totalCost = this.getTotalCost();
          this.getGlobalLinesByDayInfo();
          // this.disableRefreshButton = false;
        });
      });
    });
  }

}
