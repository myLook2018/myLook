import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { UserResolver } from './auth/components/user/user.resolver';
import { AuthGuard } from './auth/services/auth.guard';
import { InventoryComponent } from './articles/components/inventory/inventory.component';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreComponent } from './store/components/store/store.component';
import { RecomendationsComponent } from './recomendations/components/recomendations.component';
import { DashboardComponent } from './anylitics/components/dashboard/dashboard.component';
import { HomePageComponent } from './home-page/home-page.component';
import { NgModule } from '@angular/core';

export const rootRouterConfig: Routes = [
 // { path: '', component: HomePageComponent},

  { path: '', pathMatch: 'full' ,redirectTo: '/Inicio'},
  { path: 'Inicio', component: HomePageComponent, canActivate: [AuthGuard] },
  { path: '', canActivate: [AuthGuard], runGuardsAndResolvers: 'always', 
    children: [
      { path: 'Tiendas/:storeName', component: StoreComponent, resolve: { data: UserResolver }, canActivate: [AuthGuard]},
      { path: 'Tiendas/:storeName/Inventario', component: InventoryComponent, resolve: { data: UserResolver }, canActivate: [AuthGuard]},
      { path: 'Tiendas/:storeName/Estadisticas', component: DashboardComponent, resolve: { data: UserResolver }, canActivate: [AuthGuard]},
      { path: 'Tiendas/:storeName/Recomendaciones', component: RecomendationsComponent, resolve: { data: UserResolver }, canActivate: [AuthGuard]},
    ]},
  { path: 'Ingresar', component: LoginComponent, canActivate: [AuthGuard]},
  { path: 'Registrarse', component: SignupComponent, canActivate: [AuthGuard]},
  { path: 'user', component: UserComponent, resolve: { data: UserResolver }},
  { path: 'Registrar Tienda', component: RegisterComponent, canActivate: [AuthGuard]},
  
  { path: '**', redirectTo:'/Inicio'},
];

@NgModule({
  imports: [
      RouterModule.forRoot(rootRouterConfig)],
      exports: [ RouterModule ]
})
export class AppRoutingModule { }
