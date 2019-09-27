import { BrowserModule, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';

import { AngularFireModule } from 'angularfire2';
import { AngularFirestoreModule } from 'angularfire2/firestore';
import { AngularFireStorageModule } from 'angularfire2/storage';
import { AngularFireAuthModule } from 'angularfire2/auth';
import { environment } from '../environments/environment';
import { AuthGuard } from './auth/services/auth.guard';
import { AuthService } from './auth/services/auth.service';
import { DataService } from './service/dataService';
import { UserService } from './auth/services/user.service';
import { ArticleService } from './articles/services/article.service';
import { RecomendationService } from './recomendations/service/recomendationService';
import { UserResolver } from './auth/components/user/user.resolver';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { rootRouterConfig } from './app-routing.module';
import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { InventoryComponent } from './articles/components/inventory/inventory.component';
import { TagsService } from './articles/services/tags.service';
import { NgxSpinnerModule } from 'ngx-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatStepperModule } from '@angular/material/stepper';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreService } from './auth/services/store.service';
import { MatDialogModule, MatSort, MatSortModule, MatNativeDateModule } from '@angular/material';
import { DialogAlertComponent } from './dialog/dialog-alert/dialog-alert.component';
import { MatChipsModule } from '@angular/material/chips';
import { StoreFrontDialogComponent } from './articles/components/dialogs/storeFrontDialog';
import { PromoteDialogComponent } from './articles/components/dialogs/promoteDialog';
import { SuccesfulDialogComponent } from './dialog/succesful-dialog/succesful-dialog.component';
import { DeleteConfirmationDialogComponent } from './articles/components/dialogs/deleteConfirmationDialog';
import { MatAutocompleteModule } from '@angular/material';
import { StoreComponent } from './store/components/store/store.component';
// import { StoreResolver } from './store/components/store/store.resolver';
// import { ArticleResolver } from './store/components/store/store.resolver';
import { EditStoreComponent } from './store/components/dialogs/editStore';
import { AgmCoreModule } from '@agm/core';
import { MapsComponent } from './maps/maps.component';
import { MapsDialogComponent } from './dialog/maps-dialog/maps-dialog.component';
import { ErrorComponent } from './error/error.component';
import { MatListModule } from '@angular/material/list';
import { RecomendationsComponent } from './recomendations/components/recomendations.component';
import { ImageCropperModule } from 'ngx-img-cropper/index';
import { CropperSettings } from 'ngx-img-cropper/index';
import { ChartModule } from 'primeng/chart';
import { DonutchartComponent } from './anylitics/components/donutchart/donutchart.component';
import { DashboardComponent } from './anylitics/components/dashboard/dashboard.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { AnyliticService } from './anylitics/services/anylitics.service';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MomentModule } from 'ngx-moment';
import { PolarAreaComponent } from './anylitics/components/polar-area/polar-area.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TimelineComponent } from './anylitics/components/timeline/timeline.component';
import { FrontDialogComponent } from './articles/components/dialogs/frontDialog';
import { NewArticleComponent } from './articles/components/new-article/new-article.component';
import { RatingModule } from 'primeng/rating';
import { NewStoreService } from './store/service/store.service';
import { PromotionsBarComponent } from './anylitics/components/promotions-bar/promotions-bar.component';
import { HomePageComponent } from './home-page/home-page.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { OrchestratorComponent } from './orchestrator/orchestrator.component';
import { MercadopagoComponent } from './articles/components/inventory/mercadopago/mercadopago.component';
// import { ResizingCroppingImagesExample03Component } from './resizing-cropping-images-example-03.component';
import { MinimaLight, MinimaDark } from '@alyle/ui/themes/minima';
import { LyButtonModule } from '@alyle/ui/button';
import { LyResizingCroppingImageModule } from '@alyle/ui/resizing-cropping-images';
import { LyIconModule } from '@alyle/ui/icon';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { PromotionsService } from './store/components/configurations/service/promotions.service';

import {
  LyHammerGestureConfig,
  LyThemeModule,
  LY_THEME
} from '@alyle/ui';
import { HttpClientModule} from '@angular/common/http';
import { SuccessComponent } from './ecommerce/success/success.component';
import { ConfigurationsComponent } from './store/components/configurations/configurations.component';

import { LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeAR from '@angular/common/locales/es-AR';

registerLocaleData(localeAR);

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    UserComponent,
    InventoryComponent,
    SignupComponent,
    DialogAlertComponent,
    StoreFrontDialogComponent,
    SuccesfulDialogComponent,
    DeleteConfirmationDialogComponent,
    StoreComponent,
    MapsDialogComponent,
    EditStoreComponent,
    MapsComponent,
    ErrorComponent,
    RecomendationsComponent,
    DonutchartComponent,
    DashboardComponent,
    PromoteDialogComponent,
    PolarAreaComponent,
    TimelineComponent,
    FrontDialogComponent,
    PromotionsBarComponent,
    HomePageComponent,
    OrchestratorComponent,
    MercadopagoComponent,
    SuccessComponent,
    ConfigurationsComponent,
    NewArticleComponent
  ],
  imports: [
    HttpClientModule,
    NgxSpinnerModule,
    BrowserModule,
    ReactiveFormsModule,
    RouterModule.forRoot(rootRouterConfig, { useHash: false, onSameUrlNavigation: 'reload' }),
    AngularFireModule.initializeApp(environment.firebase),
    AngularFireStorageModule,
    AngularFirestoreModule, // imports firebase/firestore, only needed for database features
    AngularFireAuthModule, // imports firebase/auth, only needed for auth features
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatStepperModule,
    MatDividerModule,
    MatToolbarModule,
    MatIconModule,
    MatTableModule,
    MatDialogModule,
    MatMenuModule,
    MatSnackBarModule,
    MatSortModule,
    MatChipsModule,
    MatAutocompleteModule,
    MatTooltipModule,
    AgmCoreModule,
    MatSidenavModule,
    MatListModule,
    MatProgressBarModule,
    ImageCropperModule,
    ChartModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    MatSliderModule,
    MomentModule,
    MatCheckboxModule,
    RatingModule,
    LyResizingCroppingImageModule,
    LyButtonModule,
    LyIconModule,
    FontAwesomeModule,
    LyThemeModule.setTheme('minima-light'),
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyDmuhZx-ew-zpQzQcjiqk2yJu5OonBuSMc',
      language: 'en',
      libraries: ['geometry', 'places']
    })
  ],
  exports: [RouterModule],
  providers: [MatDatepickerModule, RecomendationService, DataService, AuthService,
    UserService, UserResolver, AuthGuard, StoreService,
    ArticleService, TagsService, AnyliticService, MatTooltipModule, PromotionsService,
    { provide: LY_THEME, useClass: MinimaLight, multi: true }, // name: `minima-light`
    { provide: LY_THEME, useClass: MinimaDark, multi: true }, // name: `minima-dark`
    { provide: HAMMER_GESTURE_CONFIG, useClass: LyHammerGestureConfig },
    { provide: LOCALE_ID, useValue: 'es-AR' },
    // ArticleResolver, StoreResolver,
    NewStoreService],
  entryComponents: [FrontDialogComponent, PromoteDialogComponent, DialogAlertComponent, StoreFrontDialogComponent, SuccesfulDialogComponent,
    DeleteConfirmationDialogComponent, MapsDialogComponent, EditStoreComponent],
  bootstrap: [AppComponent]
})
export class AppModule {
}
