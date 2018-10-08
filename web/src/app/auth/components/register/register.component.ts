import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StoreService } from '../../services/store.service';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { MatDialog, MatDialogRef } from '@angular/material';
import { DialogAlertComponent } from '../../../dialog/dialog-alert/dialog-alert.component';
import { SuccesfulDialogComponent } from '../../../dialog/succesful-dialog/succesful-dialog.component';
import { UserService } from '../../services/user.service';
import { AngularFireUploadTask, AngularFireStorage, AngularFireStorageReference } from 'angularfire2/storage';
import { finalize } from 'rxjs/operators';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  errorMessage = '';
  registerStoreFormGroup: FormGroup;
  isLinear = true;
  storeName = '';
  email: String = '';
  password: String = '';
  confirmPassword: String = '';
  urlsProfile = new Array<string>();
  urlsPortada = new Array<string>();
  filesSelected = FileList;
  urlImgProfile: string;
  urlImgShop: string;
  task: AngularFireUploadTask;
  ref: AngularFireStorageReference;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    public store: StoreService,
    public authService: AuthService,
    public userService: UserService,
    private storage: AngularFireStorage,
  ) {
    this.email = authService.getEmailToRegister();
    this.createForm();
    this.urlsProfile = [];
    this.urlsProfile.push('/assets/noProfilePic.png');
    this.urlsPortada = [];
    this.urlsPortada.push('/assets/noPhoto.png');
  }

  createForm() {
    console.log(`emailToRegistre: ${this.email}`);
    this.registerStoreFormGroup = this.fb.group({
      storeName: ['', Validators.required],
      storeMail: [this.email, Validators.email],
      ownerName: ['', Validators.required],
      // profilePh: ['', Validators.required],
      // coverPh:  ['', Validators.required],
      storePhone: ['', Validators.required],
      facebookLink: ['', Validators.required],
      storeProvince: ['', Validators.required],
      storeCity: ['', Validators.required],
      storeAddressNumber: ['', Validators.required],
      storeFloor: ['', Validators.required],
      storePosition: ['', Validators.required],
      storeDescription: ['', Validators.required],
      instagramLink: ['', Validators.required],
      twitterLink: ['', Validators.required],
      storeAddress: ['', Validators.required],
      provider: ['', Validators.required],
    });

    setTimeout(function waitTargetElem() {
      if (document.body.contains(document.getElementById('userName'))) {
        document.getElementById('userName').focus();
      } else {
        setTimeout(waitTargetElem, 100);
      }
    }, 100);
  }

  isStoreNameAvailable(stepper: MatHorizontalStepper) {
    console.log(this.storeName);
    this.userService.checkUserExistance(this.storeName).then(
      (res) => {
        if (!res) {
          this.errorMessage = 'Ya existe un usuario registrado con ese nombre.';
          console.log(this.errorMessage);
        } else {
          console.log(`el nombre de la tienda esta disponible? ${res}`);
          stepper.next();
        }
      }
    );
  }

  /*tryRegister() {

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
*/
  tryRegister() {
    const firebaseUser = {
      email: this.email,
      password: this.password
    };

    this.authService.doRegister(firebaseUser).then((userRegistered) => {
      // First Item
      const Profilefile = this.filesSelected[0];
      // Second Item
      const Portadafile = this.filesSelected[1];

      this.registerStoreFormGroup.addControl('firebaseUserId', new FormControl(userRegistered.uid, Validators.required));
      console.log(`añadimos al form ${userRegistered.uid}`);

      // Client side validation
      if (Profilefile.type.split('/')[0] !== 'image') {
        console.log('Tipo de imagen de perfil no soportado.');
        return;
      }
      if (Portadafile.type.split('/')[0] !== 'image') {
        console.log('Tipo de imagen de portada no soportado.');
        return;
      }

      // The storage Path (must be unique)
      const pathProfile = `test/${new Date().getTime()}_${Profilefile.name}`;
      const pathPortada = `test/${new Date().getTime()}_${Portadafile.name}`;
      // Optional metadata
      const customMetadata = { app: 'Mylook!' };
      // The main task / metadata is optional
      console.log('path: ' + pathProfile);
      console.log('path: ' + pathPortada);
      this.task = this.storage.upload(pathProfile, Profilefile, { customMetadata }); // suuubiendo
      console.log('Imagen de perfil guardada en myLook!');
      this.task = this.storage.upload(pathPortada, Portadafile, { customMetadata }); // suuubiendo
      console.log('Imagen de portada guardada en myLook!');

      // Progress monitoring
      // this.percentage = this.task.percentageChanges(); // usar si es necesario
      // this.snapshot = this.task.snapshotChanges(); // cambio de version

      // The file download URL
      // cambio de version/implementacion
      this.task.snapshotChanges().pipe(
        finalize(() => {
          console.log('a ver el link perfil');
          this.ref = this.storage.ref(pathProfile);
          this.ref.getDownloadURL().subscribe(url => {
            this.registerStoreFormGroup.addControl('profilePh', new FormControl(url, Validators.required));
            console.log('añadimos al form');
            console.log(url); // <-- do what ever you want with the url..
          });
          console.log('a ver el link portada');
          this.ref = this.storage.ref(pathPortada);
          this.ref.getDownloadURL().subscribe(url => {
            this.registerStoreFormGroup.addControl('coverPh', new FormControl(url, Validators.required));
            console.log('añadimos al form');
            console.log(url); // <-- do what ever you want with the url..
            // add.newUser()
            this.userService.addStore(this.registerStoreFormGroup.value);
          });
        })
      ).subscribe();
    }
    ).then(() => {
      console.log('se añadio el usuario a myLook');
      this.router.navigateByUrl('/home');
    });

  }

  detectFilesProfile(event) {
    this.filesSelected[0] = event.target.files;
    console.log(this.filesSelected[0]);
    console.log(this.filesSelected.length);
    this.urlsProfile = [];
    const files = event.target.files;
    console.log(`entramos a detectar foto de perfileee: ${files}`);
    if (files) {
      for (const file of files) {
        console.log('pasamos a render');
        const reader = new FileReader();
        reader.onload = (e: any) => { // no se que hace
          console.log(`estamos andentro del render  ${this.filesSelected[0]}`);
          console.log(file);
          this.urlsProfile.push(e.target.result);
          console.log(this.urlsProfile[0]);
        };
        reader.readAsDataURL(file);
        console.log(`aaaaaaaaaaaaaa` + this.filesSelected.length);
      }
    }
  }

  detectFilesPortada(event) {
    this.filesSelected[1] = event.target.files;
    console.log(this.filesSelected[1]);
    const files = event.target.files;
    console.log(`entramos a detectar foto de portada: ${this.filesSelected[1]}`);
    if (files) {
      for (const file of files) {
        console.log('pasamos a render');
        const reader = new FileReader();
        reader.onload = (e: any) => { // no se que hace
          console.log(this.urlsPortada.length);
          console.log(file);
          this.urlsPortada.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }
  }

}
