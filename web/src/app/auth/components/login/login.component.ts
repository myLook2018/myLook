import { Component, OnDestroy } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, Params } from '@angular/router';
import {FormBuilder, FormGroup, Validators } from '@angular/forms';
import {FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import {UserService} from '../../services/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.scss']
})

export class LoginComponent implements OnDestroy{
  _subscription: Subscription;
  loginForm: FormGroup;
  errorMessage = '';
  isLoading = false;

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);

  matcher = new MyErrorStateMatcher();

  constructor(
    public userService: UserService,
    public authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.createForm();
  }

  ngOnDestroy(): void {
    console.log("destruyendo");
    this._subscription.unsubscribe();
   // 
  }

  createForm() {
    this.loginForm = this.fb.group({
      email: ['', Validators.required ],
      password: ['', Validators.required]
    });
  }

  getUserStore(userUID) {
    return new Promise((resolve) => {
      this._subscription = this.userService.getUserInfo(userUID).subscribe(userA => {
        console.log("que carajo pasa aca ", userA )
        resolve (userA[0].storeName)
        this._subscription.unsubscribe();
      })
      });
  }

  tryFacebookLogin() {
    this.authService.doFacebookLogin()
    .then(res => {
      this.getUserStore(res.user.uid).then( res => {
        this._subscription.unsubscribe();
        this.router.navigate(['Tiendas', res]);
        }, err => {
          console.log(err);
          this.errorMessage = err;
        });
    });
  }

  tryTwitterLogin() {
    this.authService.doTwitterLogin()
    .then(res => {
      this.getUserStore(res.user.uid).then( res => {
        this._subscription.unsubscribe();
        this.router.navigate(['Tiendas', res]);
        }, err => {
          console.log(err);
          this.errorMessage = err;
        });
    });
  }

  tryGoogleLogin() {
    this.authService.doGoogleLogin()
    .then(res => {
      this.getUserStore(res.user.uid).then( res => {
        this._subscription.unsubscribe();
        this.router.navigate(['Tiendas', res]);
        }, err => {
          console.log(err);
          this.errorMessage = err;
        });
    });
  }

  tryLogin() {
    this.isLoading = true;
    console.log(this.loginForm.value);
    this.authService.doLogin(this.loginForm).then( res => {
      this.getUserStore(res.user.uid).then((nombreTienda) => {
        this.isLoading = false;
        this._subscription.unsubscribe();
        this.router.navigate(['Tiendas', nombreTienda]);
      });
    }, err => {
      this.isLoading = false;
      console.log(err);
      this.errorMessage = err;
    });
  }
}

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
