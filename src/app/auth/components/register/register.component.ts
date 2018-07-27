import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';



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
    private router: Router
  ) {
    this.createForm();
   }

  createForm() {
    this.firstFormGroup = this.fb.group({
        userName: ['', Validators.required ],
        userPicture: ['', Validators.required],
        userMail: ['', Validators.required],
        userPhone: ['', Validators.required]
    });
    this.secondFormGroup = this.fb.group({
      storeName: ['', Validators.required ],
      storePicture: ['', Validators.required],
      storeDescription: ['', Validators.required],
      attendanceHours: ['', Validators.required],
      storeAddress: ['', Validators.required],
      storeAddressNumber: ['', Validators.required],
      storeFloor: ['', Validators.required],
      storeProvince: ['', Validators.required]
    });
    this.thirdFormGroup = this.fb.group({
      storePhone: ['', Validators.required ],
      storeMail: ['', Validators.required ]

    });
  }

}
