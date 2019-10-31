import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { VoucherDialogComponent } from './modal/voucher-dialog';
import { MatDialog, MatTableDataSource } from '@angular/material';
import { AnyliticService } from 'src/app/anylitics/services/anylitics.service';
import { PromotionsService } from 'src/app/anylitics/services/promotions.service';
import { ModalCheckComponent } from './modal-check/modal-check.component';

@Component({
  selector: 'app-voucher',
  templateUrl: './voucher.component.html',
  styleUrls: ['./voucher.component.scss']
})
export class VoucherComponent implements OnInit, OnChanges {
  @Input() userStore;
  voucherCampaigns: any;
  campaignsTableDataSource: MatTableDataSource<unknown>;
  displayedColumns = ['Titulo', 'FechaInicio', 'FechaFin', 'TipoCampania', 'PrecioFinal', 'Descargar'];
  constructor(
    public dialog: MatDialog,
    private analyticsService: AnyliticService,
    private promotionsService: PromotionsService
  ) {}

  ngOnInit() {
  }

  ngOnChanges() {
    this.analyticsService.getVoucherCampaings(this.userStore.firebaseUID).then( campaings => {
      this.voucherCampaigns = campaings;
      console.log('estas son las campañas para esta tienda ', this.voucherCampaigns);
      this.campaignsTableDataSource = new MatTableDataSource(this.voucherCampaigns);
    });
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
    return this.voucherCampaigns.map(campaing => campaing.campaingCost).reduce((acc, value) => acc + value, 0);
  }

  downloadCampaign( element: any ) {
    console.log(element);
    const data = {info: element, store: this.userStore };
    this.promotionsService.downloadDocument(data, 'campaigns');
  }

}
