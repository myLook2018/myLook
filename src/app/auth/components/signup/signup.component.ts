import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, Params } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import {ErrorStateMatcher} from '@angular/material/core';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {

  signupForm: FormGroup;
  errorMessage = '';
  successMessage = '';

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);

  matcher = new MyErrorStateMatcher();

  constructor(
    public authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.createForm();
   }

   createForm() {
     this.signupForm = this.fb.group({
       email: ['', Validators.required ],
       password: ['', Validators.required]
     });
   }

   tryFacebookLogin() {
     this.authService.doFacebookLogin()
     .then(res => {
       this.router.navigate(['/home']);
     }, err => console.log(err)
     );
   }

   tryTwitterLogin() {
     this.authService.doTwitterLogin()
     .then(res => {
       this.router.navigate(['/home']);
     }, err => console.log(err)
     );
   }

   tryGoogleLogin() {
     this.authService.doGoogleLogin()
     .then(res => {
       this.router.navigate(['/home']);
     }, err => console.log(err)
     );
   }

   tryRegister(value) {
     this.authService.doRegister(value)
     .then(res => {
       console.log(res);
       this.errorMessage = '';
       this.successMessage = 'Your account has been created';
     }, err => {
       console.log(err);
       this.errorMessage = err.message;
       this.successMessage = '';
     });
   }

}
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
