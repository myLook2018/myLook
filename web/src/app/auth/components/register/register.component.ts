import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  NgZone
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormControl
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StoreService } from '../../services/store.service';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { UserService } from '../../services/user.service';
import {
  AngularFireUploadTask,
  AngularFireStorage,
  AngularFireStorageReference
} from 'angularfire2/storage';
import { DataService } from '../../../service/dataService';
import { MapsAPILoader } from '@agm/core';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  step1: any;
  step2: any;
  step3: any;

  title = 'My first AGM project';
  latitude: number;
  longitude: number;
  zoom: number;
  address: string;
  private geoCoder;

  isRegistering = false;
  errorMessage = '';
  registerStoreFormGroup: FormGroup;
  registerStoreFormGroupStep1: FormGroup;
  registerStoreFormGroupStep2: FormGroup;
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
  profileFile: FileList = null;
  portadaFile: FileList = null;
  urlImgProfile: string;
  urlImgShop: string;
  task: AngularFireUploadTask;
  ref: AngularFireStorageReference;
  normalRegister = true;
  emailAndProvider;

  @ViewChild('search')
  public searchElementRef: ElementRef;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    public store: StoreService,
    public authService: AuthService,
    public userService: UserService,
    public dataService: DataService,
    private storage: AngularFireStorage,
    private mapsAPILoader: MapsAPILoader,
    private ngZone: NgZone,
    public snackBar: MatSnackBar,
  ) {
    try {
      this.email = authService.getEmailToRegister().toString();
      this.emailAndProvider = authService.getLoginEmailAndProvider();
      if (this.emailAndProvider) {
        this.normalRegister = false;
        this.openSnackBar('Es necesario que completes la siguiente información para poder terminar tu registro.', 'x');
      }
    } catch (error) {
      console.log(error);
      this.router.navigateByUrl('/Registrarse');
    }
    this.createForm();
    this.urlsProfile = [];
    this.urlsProfile.push('/assets/noProfilePic.png');
    this.urlsPortada = [];
    this.urlsPortada.push('/assets/noPhoto.png');
  }
  ngOnInit(): void {
    // load Places Autocomplete
    this.mapsAPILoader.load().then(() => {
      this.setCurrentLocation();
      this.geoCoder = new google.maps.Geocoder();

      const autocomplete = new google.maps.places.Autocomplete(
        this.searchElementRef.nativeElement,
        {
          types: ['address']
        }
      );
      autocomplete.addListener('place_changed', () => {
        this.ngZone.run(() => {
          // get the place result
          let place: google.maps.places.PlaceResult = autocomplete.getPlace();

          // verify result
          if (place.geometry === undefined || place.geometry === null) {
            return;
          }

          // set latitude, longitude and zoom
          this.latitude = place.geometry.location.lat();
          this.longitude = place.geometry.location.lng();
          this.zoom = 15;
        });
      });
    });
  }

  // Get Current Location Coordinates
  private setCurrentLocation() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(position => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 15;
      });
    }
  }

  markerDragEnd($event: any) {
    console.log($event);
    this.latitude = $event.coords.lat;
    this.longitude = $event.coords.lng;
    this.getAddress(this.latitude, this.longitude);
  }

  getAddress(latitude, longitude) {
    this.geoCoder.geocode({ 'location': { lat: latitude, lng: longitude } }, (results, status) => {
      console.log(results);
      console.log(status);
      if (status === 'OK') {
        if (results[0]) {
          this.zoom = 15;
          this.registerStoreFormGroupStep2.get('storeAddress').setValue( results[0].formatted_address);
        } else {
          window.alert('No results found');
        }
      } else {
        window.alert('Geocoder failed due to: ' + status);
      }

    });
  }

  createForm() {
    console.log(`emailToRegistre: ${this.email}`);
    this.registerStoreFormGroupStep1 = this.fb.group({
      storeName: ['', Validators.required],
      storeMail: [this.email, Validators.email],
      storePhone: ['', Validators.required]
    });
    this.registerStoreFormGroupStep2 = this.fb.group({
      ownerName: [''],
      storeAddress: [''],
      storePosition : this.fb.group({
        latitude: [''],
        longitude: [''],
      }),
      storeFloor: [''],
      storeDept: [''],
      storeTower: [''],
      storeDescription: [''],
      facebookLink: [''],
      instagramLink: [''],
      twitterLink: [''],
      provider: [''],
      registerDate: ['']
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
    this.userService.checkUserExistance(this.storeName).then(res => {
      if (!res) {
        this.errorMessage = 'Ya se ha registrado una tienda con ese Nombre.';
        console.log(this.errorMessage);
      } else {
        console.log(`el nombre de la tienda esta disponible? ${res}`);
        stepper.next();
      }
    });
  }

  createUserForm() {
    this.userLoginForm = this.fb.group({
      email: [this.email, Validators.required]
    });
  }

  tryRegister() {
    this.isRegistering = true;
    if (!this.normalRegister || this.password === this.confirmPassword ) {
      if ( !this.normalRegister || this.password.length > 6 ) {
        this.userLoginForm.addControl(
          'password',
          new FormControl(this.password, Validators.required)
        );
        this.authService.doRegister(this.userLoginForm).then(() => {
          this.userService
            .getCurrentUser()
            .then(user => {
              this.registerStoreFormGroupStep1.addControl(
                'firebaseUserId',
                new FormControl(user.uid, Validators.required)
              );
            })
            .then(() => {
              console.log('subiendo imagen de perfil');
              this.dataService
                .uploadPicture(this.profileFile)
                .then(fileURL => {
                  this.registerStoreFormGroupStep1.addControl(
                    'profilePh',
                    new FormControl(fileURL, Validators.required)
                  );
                })
                .then(() => {
                  console.log('subiendo imagen de portada');
                  this.dataService
                    .uploadPicture(this.portadaFile)
                    .then(fileURL => {
                      this.registerStoreFormGroupStep1.addControl(
                        'coverPh',
                        new FormControl(fileURL, Validators.required)
                      );
                    })
                    .then(() => {
                      this.buildFinalForm();
                      console.log('formFinal', this.registerStoreFormGroup.value);
                      this.userService
                        .addStore(this.registerStoreFormGroup.value.store)
                        .then(() => {
                          console.log('se registro la tienda');
                        });
                    })
                    .then(() => {
                      this.authService
                        .doFirstLogin(this.userLoginForm)
                        .then(() => {
                          this.isRegistering = false;
                          this.router.navigateByUrl('/home');
                        });
                    });
                });
            });
        });
      } else {
        this.errorMessage = 'la contraseña debe ser de al menos 6 caracteres';
        this.isRegistering = false;
      }
    } else {
      this.errorMessage = `las contraseñas son diferentes`;
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
        reader.onload = (e: any) => {
          // no se que hace
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
        reader.onload = (e: any) => {
          // no se que hace
          this.urlsPortada.push(e.target.result);
        };
        reader.readAsDataURL(file);
      }
    }
  }

  clearErrors() {
    this.errorMessage = '';
  }

  buildFinalForm() {
    this.registerStoreFormGroupStep2.get('storeLatitude').setValue(this.latitude);
    this.registerStoreFormGroupStep2.get('storeLongitude').setValue(this.longitude);
    this.registerStoreFormGroupStep2.get('registerDate').setValue(new Date);
    debugger;
    const floor = this.registerStoreFormGroupStep2.get('storeFloor').value;
    this.registerStoreFormGroupStep2.get('storeFloor').setValue(floor.toString());
    const newValues = Object.assign(
      {},
      this.registerStoreFormGroupStep1.value,
      this.registerStoreFormGroupStep2.value
    );
    const valuesJson = JSON.parse(JSON.stringify(newValues));
    const form = this.toFormGroup(valuesJson);
    this.registerStoreFormGroup = new FormGroup({ store: form });
    console.log(this.registerStoreFormGroup);
  }

  toFormGroup(elements) {
    const values: any = {};
    for (const key of Object.keys(elements)) {
      values[key] = new FormControl(elements[key]);
    }
    return new FormGroup(values);
  }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 7000
    });
  }
}
