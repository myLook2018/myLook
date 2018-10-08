import { Routes } from '@angular/router';

import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { UserResolver } from './auth/components/user/user.resolver';
import { AuthGuard } from './auth/services/auth.guard';
import { InventoryComponent } from './articles/components/inventory/inventory.component';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreService } from './auth/services/store.service';
import { StoreComponent } from './store/components/store/store.component';
import { StoreResolver, ArticleResolver } from './store/components/store/store.resolver';
import { ErrorComponent } from './error/error.component';

export const rootRouterConfig: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'home', component: InventoryComponent, resolve: { data: UserResolver }},
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'signup', component: SignupComponent, canActivate: [AuthGuard] },
  { path: 'user', component: UserComponent, resolve: { data: UserResolver } },
  { path: 'register', component: RegisterComponent },
  {
    path: 'store/:storeName', component: StoreComponent,
    resolve: { data: UserResolver, articles: ArticleResolver }, runGuardsAndResolvers: 'always'
  },
  { path: '404', component: ErrorComponent }
];
