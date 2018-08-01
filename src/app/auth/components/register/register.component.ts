import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StoreService } from '../../services/store.service';
import { MatHorizontalStepper } from '../../../../../node_modules/@angular/material/stepper';
import { MatDialog, MatDialogRef } from '../../../../../node_modules/@angular/material';
import { DialogAlertComponent } from '../../../dialog/dialog-alert/dialog-alert.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  isLinear = true;


  constructor(
    private fb: FormBuilder,
    private router: Router,
    public store: StoreService,
    private dialog: MatDialog
  ) {

    this.createForm();

  }

  createForm() {
    this.firstFormGroup = this.fb.group({
      userName: ['', Validators.required],
      userPicture: ['', Validators.nullValidator],
      userMail: ['', Validators.email],
      userPhone: ['', Validators.required]
    });
    this.secondFormGroup = this.fb.group({
      storeName: ['', Validators.required],
      storePicture: ['', Validators.nullValidator],
      storeDescription: ['', Validators.required],
      attendanceHours: ['', Validators.nullValidator],
      storeAddress: ['', Validators.required],
      storeAddressNumber: ['', Validators.required],
      storeFloor: ['', Validators.nullValidator],
      storeProvince: ['', Validators.required]
    });
    this.thirdFormGroup = this.fb.group({
      storePhone: ['', Validators.required],
      storeMail: ['', Validators.required]

    });
  }

  checkUserName(userName, stepper: MatHorizontalStepper) {
    console.log('executin checkuserName');
    this.store.checkUserExistance(userName).then(
      res => {
        console.log('executin checkuserExistance');
        if (res) {
          stepper.next();
        } else {
          const dialogRef = this.dialog.open(DialogAlertComponent, { data: { userNameDialog: userName.userName } });
          dialogRef.afterClosed().subscribe(result => {
            return document.getElementById('userName').focus();
          });
        }
      },
      err => {
        console.log(err);
      });


  }
}


