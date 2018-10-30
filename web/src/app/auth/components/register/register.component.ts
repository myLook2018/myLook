import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StoreService } from '../../services/store.service';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { UserService } from '../../services/user.service';
import { AngularFireUploadTask, AngularFireStorage, AngularFireStorageReference } from 'angularfire2/storage';
import { DataService } from '../../../service/dataService';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  isRegistering = false;
  errorMessage = '';
  registerStoreFormGroup: FormGroup;
  userLoginForm: FormGroup;
  isLinear = true;
  storeName = '';
  email: string;
  password: string;
  confirmPassword: string;
  pathProfile;
  pathPortada;
  urlsProfile = new Array<string>();
  urlsPortada = new Array<string>();
  profileFile: FileList;
  portadaFile: FileList;
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
    public dataService: DataService,
    private storage: AngularFireStorage,
  ) {
    this.email = authService.getEmailToRegister().toString();
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

    this.createUserForm();

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
          this.errorMessage = 'Ya se ha registrado una tienda con ese Nombre.';
          console.log(this.errorMessage);
        } else {
          console.log(`el nombre de la tienda esta disponible? ${res}`);
          stepper.next();
        }
      }
    );
  }

  createUserForm() {
    this.userLoginForm = this.fb.group({
      email: [this.email, Validators.required],
    });
  }


  tryRegister() {
    this.isRegistering = true;
    if (this.password === this.confirmPassword) {
      if (this.password.length > 6) {

        this.userLoginForm.addControl('password', new FormControl(this.password, Validators.required));
        this.authService.doRegister(this.userLoginForm).then(() => {
          this.userService.getCurrentUser().then((user) => {
            this.registerStoreFormGroup.addControl('firebaseUserId', new FormControl(user.uid, Validators.required));
          }
          ).then(() => {
            this.dataService.uploadPicture(this.profileFile).then((fileURL) => {
              this.registerStoreFormGroup.addControl('profilePh', new FormControl(fileURL, Validators.required));
            }).then(() => {
              this.dataService.uploadPicture(this.portadaFile).then((fileURL) => {
                this.registerStoreFormGroup.addControl('coverPh', new FormControl(fileURL, Validators.required));
              }).then(() => {
                this.userService.addStore(this.registerStoreFormGroup.value).then(() => { });
              }).then(() => {
                this.authService.doFirstLogin(this.userLoginForm).then(() => {
                  this.isRegistering = false;
                  this.router.navigateByUrl('/home');
                });
              });
            });
          });
        });
      } else {
        console.log(`la contraseña debe ser de al menos 6 caracteres`);
        this.isRegistering = false;
      }
    } else {
      console.log(`las contraseñas son diferentes`);
      this.isRegistering = false;
    }
  }

  detectFilesProfile(event) {
    this.profileFile = event.target.files;
    this.urlsProfile = [];
    const files = event.target.files;
    if (files) {
      for (const file of files) {
        const reader = new FileReader();
        reader.onload = (e: any) => { // no se que hace
          this.urlsProfile.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }
  }

  detectFilesPortada(event) {
    this.portadaFile = event.target.files;
    this.urlsPortada = [];
    const files = event.target.files;
    if (files) {
      for (const file of files) {
        const reader = new FileReader();
        reader.onload = (e: any) => { // no se que hace
          this.urlsPortada.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }
  }

}
