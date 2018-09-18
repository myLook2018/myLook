import { Resolve, Router, ActivatedRouteSnapshot } from '@angular/router';
import { Injectable } from '../../../../../node_modules/@angular/core';
import { StoreService } from '../../../auth/services/store.service';

@Injectable()
export class StoreResolver implements Resolve<any> {

    constructor(private storeService: StoreService, private router: Router) {

    }

    resolve(route: ActivatedRouteSnapshot): Promise<any> {
        const storeName = route.params['storeName'];
        return this.storeService.getStoreData(storeName);
    }


}

@Injectable()
export class ArticleResolver implements Resolve<any> {

    constructor(private storeService: StoreService, private router: Router) {

    }

    resolve(route: ActivatedRouteSnapshot): Promise<any> {
        const storeName = route.params['storeName'];
        return this.storeService.getStoreArticles(storeName);
    }


}
