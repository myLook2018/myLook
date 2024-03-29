import { Component, OnDestroy } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, Params } from '@angular/router';
import {FormBuilder, FormGroup, Validators } from '@angular/forms';
import {FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';
import {UserService} from '../../services/user.service';
import { Subscription } from 'rxjs';
import { ToastsService, TOASTSTYPES } from 'src/app/service/toasts.service';

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

  // tslint:disable-next-line: no-use-before-declare
  matcher = new MyErrorStateMatcher();

  constructor(
    public toastService: ToastsService,
    public userService: UserService,
    public authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.createForm();
  }

  ngOnDestroy(): void {
    if(this._subscription){
      console.log('destruyendo ***************************************************************', this._subscription);
      this._subscription.unsubscribe();
    }
   //
  }

  createForm() {
    this.loginForm = this.fb.group({
      email: ['', Validators.required ],
      password: ['', Validators.required]
    });
  }

  getUserStore(userUID) {
    return new Promise((resolve, reject) => {
      console.log('la subs ', this._subscription);
      console.log('el firebaseID ', userUID);
      this._subscription = this.userService.getUserInfo(userUID).subscribe(userA => {
        console.log('que carajo pasa aca ', userA);
        const result = userA[0] ? userA[0].storeName : null;
        resolve (result);
      });
    });
  }

  tryFacebookLogin() {
    this.authService.doFacebookLogin()
    .then(res => {
      this.getUserStore(res.user.uid).then( store => {
        this._subscription.unsubscribe();
        if (!store) { this.router.navigate(['Registrar-Tienda']);
        } else {
           this.router.navigate(['Tiendas', res]);
         }
        }, err => {
          console.log(err);
          this.errorMessage = this.translateError(err);
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
          this.errorMessage = this.translateError(err);
        });
    });
  }

  tryGoogleLogin() {
    this.authService.doGoogleLogin()
    .then(res => {
      this.getUserStore(res.user.uid).then( store => {
        this._subscription.unsubscribe();
        if (!store) { this.router.navigate(['Registrar-Tienda']);
        } else {
           this.router.navigate(['Tiendas', res]);
         }
        }, err => {
          console.log(err);
          this.errorMessage = this.translateError(err);
        });
    });
  }

  async tryLoginWithSocialNetwork(network) {
    let trylogin;
    switch (network) {
      case 'google':
        trylogin = await this.tryGoogleLogin();
        break;
      case 'facebook':
        trylogin = await this.tryFacebookLogin();
        break;
    }
  }

  tryLogin() {
    this.isLoading = true;
    console.log(this.loginForm.value);
    this.authService.doLogin(this.loginForm).then( res => {
      console.log(`tamadre`, res);
      this.getUserStore(res.user.uid).then((nombreTienda) => {
        this.isLoading = false;
        this._subscription.unsubscribe();
        this.router.navigate(['Tiendas', nombreTienda]);
      });
    }, err => {
      console.log(err);
      this.errorMessage = this.translateError(err);
      this.isLoading = false;
    });
  }

  translateError(error: string) {
    let message = '';
    switch (true) {
      case (error.includes('password is invalid')):
        message = 'Constraseña incorrecta.';
        break;
      case (error.includes('no user record')):
        message = 'El email ingresado no se encuentra registrado en myLook.';
        break;
      case (error.includes('many unsuccessful login attempts')):
        // tslint:disable-next-line: max-line-length
        message = 'Verifica por favor los datos ingresados. Si los intentos fallidos continúan, bloquearemos temporalmente tu cuenta por seguridad.';
        break;
      case (error.includes('auth/user-not-found')):
        message = 'No se ha encontrado usuario registrado con ese email, verifica los datos ingresados.';
        break;
      case (error.includes('auth/invalid-email')):
        message = 'Email ingresado invalido.';
        break;
      default:
        message = error;
        break;
    }
    return message;
  }

  restartPassword() {
    this.authService.sendResetPasswordEmail(this.loginForm.get('email').value).then( res => {
      console.log('res', res);
      this.toastService.showToastMessage('Email enviado', TOASTSTYPES.INFO, 'Te hemos enviado un email para reestablecer tu contraseña.');
    }).catch(error => {
      console.log('el error', error);
      this.toastService.showToastMessage('Error', TOASTSTYPES.ERROR, this.translateError(error.code));
    });
  }
}


export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
