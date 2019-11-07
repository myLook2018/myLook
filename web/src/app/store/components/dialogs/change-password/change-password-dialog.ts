import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { FormControl } from '@angular/forms';
import * as firebase from 'firebase';
import { ToastsService, TOASTSTYPES} from 'src/app/service/toasts.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password-dialog.html',
  styleUrls: ['./change-password-dialog.scss']
})
export class ChangePasswordDialogComponent implements OnInit {
  actualPassword: FormControl;
  newPassword: FormControl;
  confirmPassword: FormControl;
  firebaseUser: firebase.User;
  isLoading = false;
  userData: any;
  credential: firebase.auth.EmailAuthProvider;

  constructor(
    public dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    private toastsService: ToastsService,

    @Inject(MAT_DIALOG_DATA) public data) {
      this.firebaseUser = firebase.auth().currentUser;
      this.userData = data;
      this.actualPassword = new FormControl('');
      this.newPassword = new FormControl('');
      this.confirmPassword = new FormControl('');
    }

  ngOnInit() {
  }

  closeModal(): void {
    this.dialogRef.close();
    console.log('me cerraron el dialog');
  }

  tryChangePassword() {
    this.isLoading = !this.isLoading;
    console.log('intentando cambiar la pass');
    this.tryReAuth();
  }

  tryReAuth() {
    // First you get the current logged in user
    const cpUser = firebase.auth().currentUser;

    /*Then you set credentials to be the current logged in user's email
    and the password the user typed in the input named "old password"
    where he is basically confirming his password just like facebook for example.*/

    const credentials = firebase.auth.EmailAuthProvider.credential( cpUser.email, this.actualPassword.value);

    // Reauthenticating here with the data above
    cpUser.reauthenticateWithCredential(credentials).then( success => {
        if ( this.newPassword.value !== this.confirmPassword.value) {
          // mostrar toast de alerta de que las contraseñas son incorrectas
          console.log('contraseñas incorrectas');
          this.newPassword.setErrors({ passwordNotMatching: true });
          this.confirmPassword.setErrors({ passwordNotMatching: true });
          this.isLoading = false;
        } else if ( this.newPassword.value.length < 6) {
          // mostrat toast de contraseña muy corta
          console.log('contraseña muy corta');
          this.newPassword.setErrors({ passwordTooShort: true });
          this.isLoading = false;
        } else {
          // ahora mostramos que se cambia exitosamente

        /* Update the password to the password the user typed into the
          new password input field */
        cpUser.updatePassword(this.newPassword.value).then( () => {
          // Success
          // aca podriamos mostrar verdaderamente que revento
          console.log('salio todo beeeennnn');
          this.toastsService.showToastMessage('Contraseña actualizada correctamente', TOASTSTYPES.SUCCESS);
          this.isLoading = false;
          this.closeModal();
        }).catch( error => {
          // Failed in firebase
          // mostrar de alguna forma el error en firebase
          this.toastsService.showToastMessage('Error inesperado, intente nuevamente', TOASTSTYPES.ERROR);
          console.log('revento', error);
          this.isLoading = false;
        });
        }
      },
      error => {
        console.log(error);
        if (error.code === 'auth/wrong-password') {
          this.actualPassword.setErrors({ wrongPassword: true });
          // mostrar error que la contraseña no es valida
          console.log('metiste mal la pass');
          this.isLoading = false;
        }
      }
    );
    console.log(credentials);
  }
}
