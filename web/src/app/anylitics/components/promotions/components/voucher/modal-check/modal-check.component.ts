import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { FormControl } from '@angular/forms';
import { DataService } from 'src/app/service/dataService';
import { ToastsService, TOASTSTYPES } from 'src/app/service/toasts.service';

@Component({
  selector: 'app-modal-check',
  templateUrl: './modal-check.component.html',
  styleUrls: ['./modal-check.component.css']
})
export class ModalCheckComponent implements OnInit {
  voucherCode: FormControl;
  isLoading = false;

  constructor(
    public dialogRef: MatDialogRef<ModalCheckComponent>,
    private dataService: DataService,
    private toastsService: ToastsService,

    @Inject(MAT_DIALOG_DATA) public data
    ) {
      this.voucherCode = new FormControl('');
    }

    ngOnInit() {
    }

    tryActivateVoucher() {
      this.isLoading = true;
      this.dataService.tryActivateVoucher(this.voucherCode.value).then( result => {
        this.isLoading = false;
        if (result.title === 'success') {
          console.log('salio todo bien, activado');
          this.toastsService.showToastMessage('Cupón Activado', TOASTSTYPES.SUCCESS, 'Se registró el uso del cupón exitosamente.');
        } else if (result.title === 'used') {
          this.toastsService.showToastMessage('Cupón ya utilizado', TOASTSTYPES.WARN,
                                              `El cupón fue utilizado el ${this.formatDate(result.usedDate.toDate())}.`);
          console.log('Ya fue usado');
        } else {
          this.toastsService.showToastMessage('Error', TOASTSTYPES.ERROR, 'El código del cupón no es válido.');
          console.log('Codigo invalido');
        }
      }).catch( error => {
        this.isLoading = false;
      });
    }

    formatDate(date: Date) {
      console.log('el date que tiramos', date);
      const monthNames = [
        'Enero', 'Febrero', 'Marzo',
        'Abril', 'Mayo', 'Junio', 'Julio',
        'Agosto', 'Septiembre', 'Octubre',
        'Noviembre', 'Diciembre'
      ];

      const dayOfWeek = [
        'Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sábado'
      ];

      const dayIndex = date.getDay();
      const day = date.getDate();
      const monthIndex = date.getMonth();
      const year = date.getFullYear();
      const hora = date.getHours();
      const minutos = date.getMinutes();
      const segundos = date.getSeconds();

      return  dayOfWeek[dayIndex] + ', ' + day + ' de ' + monthNames[monthIndex] + ' de ' + year +
        ' a las ' + hora + ':' + minutos + 'hs.';
    }

    closeModal(): void {
      this.dialogRef.close();
      console.log('me cancelaron');
    }

}
