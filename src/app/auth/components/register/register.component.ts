import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StoreService } from '../../services/store.service';
import { MatHorizontalStepper } from '../../../../../node_modules/@angular/material/stepper';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
DialogData

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
    public dialog: MatDialog
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
    console.log('Mateo');
    if (!this.store.checkUserExistance(userName)) {
      stepper.next();
    } else {
      
    }


  }
}

@Component({
  selector: 'dialog-overview-example',
  templateUrl: 'dialog-overview-example.html',
  styleUrls: ['dialog-overview-example.css'],
})
export class DialogOverviewExample {

  animal: string;
  name: string;

  constructor(public dialog: MatDialog) {}

  openDialog(): void {
    const dialogRef = this.dialog.open(DialogOverviewExampleDialog, {
      width: '250px',
      data: {name: this.name, animal: this.animal}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.animal = result;
    });
  }

}

@Component({
  selector: 'dialog-overview-example-dialog',
  templateUrl: 'dialog-overview-example-dialog.html',
})
export class DialogOverviewExampleDialog {

  constructor(
    public dialogRef: MatDialogRef<DialogOverviewExampleDialog>) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}

