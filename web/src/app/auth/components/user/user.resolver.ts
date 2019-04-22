import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { DataService } from '../../../service/dataService';
import { StoreModel } from '../../models/store.model';

@Injectable()
export class UserResolver implements Resolve<StoreModel> {
  user: StoreModel = new StoreModel();
  constructor(public dataService: DataService, private router: Router) { }

  resolve(route: ActivatedRouteSnapshot): Promise<StoreModel> {
    return new Promise((resolve, reject) => {
      if(this.dataService.isNewUser){
        console.log(`vamos a pedir EL USUARIO A FIREBASE`)
        this.dataService.refreshLocalUserInformation()
        .then(res => {
          this.user = res;
          resolve(this.user);
        }, err => {
          console.log(err);
          this.router.navigate(['/login']);
          return reject(err);
        });
      } else {
        console.log(`PASAMOS EL USUARIO LOCAL`)
        resolve(this.user)}
      });
    }
  }
