import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { UserResolver } from './auth/components/user/user.resolver';
import { AuthGuard } from './auth/services/auth.guard';
import { InventoryComponent } from './articles/components/inventory/inventory.component';
import { NewArticleComponent } from './articles/components/new-article/new-article.component';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreComponent } from './store/components/store/store.component';
import { RecomendationsComponent } from './recomendations/components/recomendations.component';
import { DashboardComponent } from './anylitics/components/dashboard/dashboard.component';
import { HomePageComponent } from './home-page/home-page.component';
import { NgModule } from '@angular/core';
import { ConfigurationsComponent } from './store/components/configurations/configurations.component';

export const rootRouterConfig: Routes = [
 // { path: '', component: HomePageComponent},

  { path: '', pathMatch: 'full' , redirectTo: '/Ingresar'},
  // { path: 'Inicio', component: HomePageComponent},
  { path: '', runGuardsAndResolvers: 'always',
    children: [
      { path: 'Tiendas/:storeName', component: StoreComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Catalogo', component: InventoryComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Estadisticas', component: DashboardComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Recomendaciones', component: RecomendationsComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Configuracion', component: ConfigurationsComponent, resolve: { data: UserResolver }},
      { path: 'Tiendas/:storeName/Nuevo-Articulo', component: NewArticleComponent, resolve: { data: UserResolver }},
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
