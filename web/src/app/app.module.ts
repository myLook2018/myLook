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
import { UserService } from './auth/services/user.service';
import { ArticleService } from './articles/services/article.service';
import { UserResolver } from './auth/components/user/user.resolver';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { rootRouterConfig } from './app-routing.module';
import { LoginComponent } from './auth/components/login/login.component';
import { UserComponent } from './auth/components/user/user.component';
import { RegisterComponent } from './auth/components/register/register.component';
import { InventoryComponent } from './articles/components/inventory/inventory.component';


import { MatMenuModule} from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBarModule} from '@angular/material/snack-bar';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatStepperModule } from '@angular/material/stepper';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { SignupComponent } from './auth/components/signup/signup.component';
import { StoreService } from './auth/services/store.service';
import { MatDialogModule, MatSort, MatSortModule } from '@angular/material';
import { DialogAlertComponent } from './dialog/dialog-alert/dialog-alert.component';
import { UpLoadArticleComponent } from './articles/components/dialogs/uploadArticle';
import { SuccesfulDialogComponent } from './dialog/succesful-dialog/succesful-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserComponent,
    RegisterComponent,
    InventoryComponent,
    SignupComponent,
    DialogAlertComponent,
    UpLoadArticleComponent,
    SuccesfulDialogComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    RouterModule.forRoot(rootRouterConfig, { useHash: false }),
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
    MatSortModule
  ],
  exports: [RouterModule],
  providers: [AuthService, UserService, UserResolver, AuthGuard, StoreService, ArticleService],
  entryComponents: [DialogAlertComponent, UpLoadArticleComponent, SuccesfulDialogComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
