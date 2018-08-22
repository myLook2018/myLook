import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StoreService } from '../../services/store.service';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { MatDialog, MatDialogRef } from '@angular/material';
import { DialogAlertComponent } from '../../../dialog/dialog-alert/dialog-alert.component';
import { SuccesfulDialogComponent } from '../../../dialog/succesful-dialog/succesful-dialog.component';


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
  email: String;
  private targetInput = '';
  urls = new Array<string>();
  filesSelected: FileList;
  urlImgProfile: string;
  urlImgShop: string;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    public store: StoreService,
    private dialog: MatDialog,
    public authService: AuthService
  ) {
    this.email = authService.getEmailToRegister();
    this.createForm();
    this.urls = [];
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

    setTimeout(function waitTargetElem() {
      if (document.body.contains(document.getElementById('userName'))) {
        document.getElementById('userName').focus();
      } else {
        setTimeout(waitTargetElem, 100);
      }
    }, 100);
  }



  checkUserName(userName, stepper: MatHorizontalStepper) {
    this.store.checkUserExistance(userName).then(
      res => {
        if (res) {
          stepper.next();
          setTimeout(function waitTargetElem() {
            if (document.body.contains(document.getElementById('storeName'))) {
              document.getElementById('storeName').focus();
            } else {
              setTimeout(waitTargetElem, 100);
            }
          }, 100);

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

  checkStoreName(value, stepper: MatHorizontalStepper) {
    this.store.checkStoreExistance(value.storeName).then(
      res => {
        if (res) {
          stepper.next();
          setTimeout(function waitTargetElem() {
            if (document.body.contains(document.getElementById('storePhone'))) {
              document.getElementById('storePhone').focus();
            } else {
              setTimeout(waitTargetElem, 100);
            }
          }, 100);
        }
      });
  }

  tryRegister(userForm, storeForm, storeContactForm) {
    const user = {
      userMail: userForm.userMail,
      userName: userForm.userName,
      userPhone: userForm.userPhone,
      userPicture: '/images/userImage2'
    };
    const store = {
      storeAddresNumber: storeForm.storeAddressNumber,
      storeAddress: storeForm.storeAddress,
      storeDescription: storeForm.storeDescription,
      storeFloor: storeForm.storeFloor,
      storeMail: storeContactForm.storeMail,
      storeName: storeForm.storeName,
      storePhone: storeContactForm.storePhone
    };

    this.store.tryRegisterStore(user, store).then(
      res => {
        if (res) {
          const dialogRef = this.dialog.open(SuccesfulDialogComponent);
          dialogRef.afterClosed().subscribe(result => {
            this.router.navigateByUrl('/home');
          });

        }


      });
  }

  detectFiles(event , index) {
    this.filesSelected = event.target.files;
  //  this.urls = [];
    const files = event.target.files;
    if (files) {
      for (const file of files) {
        console.log('pasamos a render');
        const reader = new FileReader();
        reader.onload = (e: any) => {
          console.log(this.urls.length);
          console.log(file);
          if ( index === 0 ) {
            this.urls[0] = e.target.result;
           } else { this.urls[1] = e.target.result; }
        };
        reader.readAsDataURL(file);
      }
    }
  }
}
