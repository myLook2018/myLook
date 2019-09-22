import { Component, OnInit, OnDestroy } from '@angular/core';
import { StoreModel } from 'src/app/auth/models/store.model';
import { UserService } from 'src/app/auth/services/user.service';
import { AuthService } from 'src/app/auth/services/auth.service';
import {
  Router,
  ActivatedRoute,
  Event,
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart
} from '@angular/router';
import {} from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { DataService } from '../service/dataService';
import { AuthGuard } from '../auth/services/auth.guard';
@Component({
  selector: 'app-orchestrator',
  templateUrl: './orchestrator.component.html',
  styleUrls: ['./orchestrator.component.css']
})
export class OrchestratorComponent implements OnInit, OnDestroy {
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  isLogedIn = false;
  constructor(
    public userService: UserService,
    public authService: AuthService,
    public dataServide: DataService,
    public authGuard: AuthGuard,
    private router: Router,
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService
  ) {
    this.userStore.profilePh = '/assets/noProfilePic.png';
    this.router.events.subscribe((event: Event) => {
      switch (true) {
        case event instanceof NavigationStart: {
          this.spinner.show();
          break;
        }
        case event instanceof NavigationEnd: {
          this.authGuard.canActivate();
          this.refreshUserInformation();
          setTimeout(() => {
            this.spinner.hide();
          }, 2000);
          console.log(this.userStore);
        }
        case event instanceof NavigationCancel:
        case event instanceof NavigationError: {
          break;
        }
        default: {
          break;
        }
      }
    });
  }

  ngOnInit() {
    console.log('vieja no entiendo nada');
    this.route.data.subscribe(routeData => {
      const data = routeData['data'];
      if (data) {
        this.userStore = data;
      }
    });
    console.log('iniciando orchestrator');
  }

  refreshUserInformation() {
    this.dataServide.getStoreInfo().then(store => {
      console.log(store);
      this.userStore = store;
      if (store.firebaseUserId) {
        this.isLogedIn = true;
        this.authGuard.canActivate();
        console.log(
          'is loggued in ----------------------------------------------------------------------------------------'
        );
        console.log(
          'Ya validamos -----------------------------------------------------------------------------------------'
        );
      } else {
        this.isLogedIn = false;
        console.log(
          'Ya validamos -----------------------------------------------------------------------------------------'
        );
      }
    });
  }

  logout() {
    this.authService.doLogout().then(
      res => {
        this.dataServide.cleanCache();
        this.router.navigate(['Inicio']);
        this.isLogedIn = false;
      },
      error => {
        console.log('Error al desloguear');
      }
    );
  }

  hideSpinner(event) {
    console.log('me dijieron que me esconda, yo el spinner');
    this.spinner.hide();
  }

  ngOnDestroy(): void {}
  goToProfile() {
    this.router.navigate(['Tiendas', this.userStore.storeName]);
  }

  goToInventory() {
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Catalogo`]);
  }

  goToRecomendations() {
    this.router.navigate([
      `/Tiendas/${this.userStore.storeName}/Recomendaciones`
    ]);
  }

  goToAnalytics() {
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Estadisticas`]);
  }

  editStoreInfo() {
    alert('not implemented yet, Xdxd');
  }
}
