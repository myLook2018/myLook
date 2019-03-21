import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, Router} from '@angular/router';
import { AngularFireAuth } from 'angularfire2/auth';
import { UserService } from './user.service';
import { DataService } from '../../service/dataService';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(
    public afAuth: AngularFireAuth,
    public userService: UserService,
    private router: Router,
    public dataService: DataService
  ) {}

  canActivate(): Promise<boolean> {
    return new Promise((resolve, reject) => {
      const user = this.dataService.getStoreInfo();
      if(user.firebaseUID !== '' && !this.router.url.toString().includes('Tiendas')) {
        console.log("*********************************** esta logeado y con url -" , this.router.url)
        this.router.navigate(['Tiendas', user.storeName]);
        return resolve(false);
      } else {
        if( user.firebaseUID !== '' && this.router.url.toString().includes('Tiendas')){
          console.log("*********************************** esta logeado y con url +" , this.router.url)
          return resolve(true);
        } else {
          if( user.firebaseUID === '' && this.router.url.toString().includes('Tiendas')) {
            console.log("*********************************** esta logeado y con url +" , this.router.url)
            this.router.navigate(['Inicio']);
            return resolve(false);
          } else {
            console.log("*********************************** no esta logueado y no va a tienda")
            return resolve(true);
          }

        }
      }
    });
  }
}
