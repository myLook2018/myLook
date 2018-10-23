import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { StoreModel } from '../../models/store.model';

@Injectable()
export class UserResolver implements Resolve<StoreModel> {

  constructor(public userService: UserService, private router: Router) { }

  resolve(route: ActivatedRouteSnapshot): Promise<StoreModel> {

    const user = new StoreModel();

    return new Promise((resolve, reject) => {
      this.userService.getCurrentUser()
        .then(res => {
          user.profilePh = res.photoURL;
          console.log( `fotooo` +  user.profilePh);
          user.storeName = res.displayName;
          user.firebaseUserId = res.uid;
          user.provider = res.providerData[0].providerId;
          resolve(user);
        }, err => {
          console.log(err);
          this.router.navigate(['/login']);
          return reject(err);
        });
      });
    }
  }
