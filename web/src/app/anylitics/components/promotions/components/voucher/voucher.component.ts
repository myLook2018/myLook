import { Component, OnInit, Input } from '@angular/core';
import { VoucherDialogComponent } from './modal/voucher-dialog';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-voucher',
  templateUrl: './voucher.component.html',
  styleUrls: ['./voucher.component.scss']
})
export class VoucherComponent implements OnInit {
  @Input() userStore;
  constructor(
    public dialog: MatDialog
  ) { }

  ngOnInit() {
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

  // promoteArticle(data, article, storeUID) {
  //   console.log(data);
  //   console.log(article);
  //   this.articleService.promoteArticle(data, article, storeUID);
  // }

}
