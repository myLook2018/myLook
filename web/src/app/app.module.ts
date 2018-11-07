import { BrowserModule } from '@angular/platform-browser';
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
import { ArticleDialogComponent } from './articles/components/dialogs/articleDialog';
import { PromoteDialogComponent } from './articles/components/dialogs/promoteDialog';
import { SuccesfulDialogComponent } from './dialog/succesful-dialog/succesful-dialog.component';
import { DeleteConfirmationDialogComponent } from './articles/components/dialogs/deleteConfirmationDialog';
import { MatAutocompleteModule } from '@angular/material';
import { StoreComponent } from './store/components/store/store.component';
import { StoreResolver } from './store/components/store/store.resolver';
import { ArticleResolver } from './store/components/store/store.resolver';
import { EditStoreComponent } from './store/components/dialogs/editStore';
import { AgmCoreModule } from '@agm/core';
import { MapsComponent } from './maps/maps.component';
import { MapsDialogComponent } from './dialog/maps-dialog/maps-dialog.component';
import { ErrorComponent } from './error/error.component';
import { MatListModule } from '@angular/material/list';
import { RecomendationsComponent } from './recomendations/components/recomendations.component';
import { ImageCropperModule } from 'ngx-image-cropper';
import { ImageCropperComponent, CropperSettings } from 'ngx-img-cropper';
import { ChartModule } from 'primeng/chart';
import { DonutchartComponent } from './anylitics/components/donutchart/donutchart.component';
import { DashboardComponent } from './anylitics/components/dashboard/dashboard.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { AnyliticService } from './anylitics/components/services/anylitics.service';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { MomentModule } from 'ngx-moment';
import { PolarAreaComponent } from './anylitics/components/polar-area/polar-area.component';
import { MatCheckboxModule } from '@angular/material/checkbox';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserComponent,
    RegisterComponent,
    InventoryComponent,
    SignupComponent,
    DialogAlertComponent,
    ArticleDialogComponent,
    SuccesfulDialogComponent,
    DeleteConfirmationDialogComponent,
    StoreComponent,
    MapsDialogComponent,
    EditStoreComponent,
    MapsComponent,
    ErrorComponent,
    RecomendationsComponent,
    ImageCropperComponent,
    DonutchartComponent,
    DashboardComponent,
    PromoteDialogComponent,
    PolarAreaComponent
  ],
  imports: [
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
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyC7gRAUi_0C-w5gqsWIVp0IKyEZT-3UiUA'
    })
  ],
  exports: [RouterModule],
  providers: [MatDatepickerModule, RecomendationService, DataService, AuthService, UserService, UserResolver, AuthGuard, StoreService,
    ArticleService, TagsService, AnyliticService, ArticleResolver, StoreResolver],
  entryComponents: [PromoteDialogComponent, DialogAlertComponent, ArticleDialogComponent, SuccesfulDialogComponent,
    DeleteConfirmationDialogComponent, MapsDialogComponent, EditStoreComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
