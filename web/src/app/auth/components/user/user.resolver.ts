import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { FirebaseUserModel } from '../../models/user.model';

@Injectable()
export class UserResolver implements Resolve<FirebaseUserModel> {

  constructor(public userService: UserService, private router: Router) { }

  resolve(route: ActivatedRouteSnapshot): Promise<FirebaseUserModel> {

    const user = new FirebaseUserModel();

    return new Promise((resolve, reject) => {
      this.userService.getCurrentUser()
        .then(res => {
          user.image = res.photoURL;
          user.name = res.displayName;
          console.log('1');
          user.userId = res.uid;
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
