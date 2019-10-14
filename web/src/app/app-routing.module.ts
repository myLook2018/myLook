import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { UserResolver } from './auth/components/user/user.resolver';
import { InventoryComponent } from './articles/components/inventory/inventory.component';
import { NewArticleComponent } from './articles/components/new-article/new-article.component';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreComponent } from './store/components/store/store.component';
import { RecomendationsComponent } from './recomendations/components/recomendations.component';
import { DashboardComponent } from './anylitics/components/dashboard/dashboard.component';
import { NgModule } from '@angular/core';
import { ConfigurationsComponent } from './store/components/configurations/configurations.component';
import { StoreFrontComponent } from './articles/components/inventory/storeFront/storeFront';
import { AnyliticsDashboardComponent } from './anylitics/components/promotions/analytics-dashboard.component';


export const rootRouterConfig: Routes = [
 // { path: '', component: HomePageComponent},

  { path: '', pathMatch: 'full' , redirectTo: '/Ingresar'},
  // { path: 'Inicio', component: HomePageComponent},
  { path: '', runGuardsAndResolvers: 'always',
    children: [
      { path: 'Tiendas/:storeName', component: StoreComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Catalogo', component: InventoryComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Estadisticas', component: DashboardComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Promociones', component: AnyliticsDashboardComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Recomendaciones', component: RecomendationsComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Configuracion', component: ConfigurationsComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Ver-Articulo/:id', component: NewArticleComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Editar-Articulo/:id', component: NewArticleComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Nuevo-Articulo', component: NewArticleComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Vidriera', component: StoreFrontComponent, resolve: { data: UserResolver }},
    ]},
  { path: 'Ingresar', component: LoginComponent},
  { path: 'Registrarse', component: SignupComponent},
  { path: 'user', component: UserComponent, resolve: { data: UserResolver }},
  { path: 'Registrar-Tienda', component: RegisterComponent},

  { path: '**', redirectTo: '/Ingresar'},
];

@NgModule({
  imports: [
      RouterModule.forRoot(rootRouterConfig)],
      exports: [ RouterModule ]
})
export class AppRoutingModule { }
