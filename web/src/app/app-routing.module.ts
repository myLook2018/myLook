import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { UserResolver } from './auth/components/user/user.resolver';
import { AuthGuard } from './auth/services/auth.guard';
import { InventoryComponent } from './articles/components/inventory/inventory.component';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreService } from './auth/services/store.service';
import { StoreComponent } from './store/components/store/store.component';
// import { StoreResolver, ArticleResolver } from './store/components/store/store.resolver';
import { ErrorComponent } from './error/error.component';
import { RecomendationsComponent } from './recomendations/components/recomendations.component';
import { DonutchartComponent } from './anylitics/components/donutchart/donutchart.component';
import { DashboardComponent } from './anylitics/components/dashboard/dashboard.component';
import { HomePageComponent } from './home-page/home-page.component';
import { NgModule } from '@angular/core';

export const rootRouterConfig: Routes = [
 // { path: '', component: HomePageComponent},

  { path: '', pathMatch: 'full' ,redirectTo: '/Inicio'},
  { path: 'Inicio', component: HomePageComponent},
  { path: '', runGuardsAndResolvers: 'always',
    children: [
      { path: 'Tiendas/:storeName', component: StoreComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Inventario', component: InventoryComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Estadisticas', component: DashboardComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Recomendaciones', component: RecomendationsComponent, resolve: { data: UserResolver }},
    ]},
  { path: 'Ingresar', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'Registrarse', component: SignupComponent, canActivate: [AuthGuard] },
  { path: 'user', component: UserComponent, resolve: { data: UserResolver } },
  { path: 'Registrar Tienda', component: RegisterComponent },
  
  { path: '404', component: ErrorComponent },
];

@NgModule({
  imports: [
      RouterModule.forRoot(rootRouterConfig)],
      exports: [ RouterModule ]
})
export class AppRoutingModule { }
