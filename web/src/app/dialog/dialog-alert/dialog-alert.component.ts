import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';


@Component({
  selector: 'app-alert-dialog',
  templateUrl: './dialog-alert.component.html'
})
export class DialogAlertComponent {
  userNameDialog: String;
  constructor(@Inject(MAT_DIALOG_DATA) userName: any) {
  this.userNameDialog = userName.userNameDialog;

  }

}
