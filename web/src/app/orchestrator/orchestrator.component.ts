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
import { AnyliticService } from '../anylitics/services/anylitics.service';
import { PromotionsService } from '../anylitics/services/promotions.service';
@Component({
  selector: 'app-orchestrator',
  templateUrl: './orchestrator.component.html',
  styleUrls: ['./orchestrator.component.scss']
})
export class OrchestratorComponent implements OnInit, OnDestroy {
  FirebaseUser = new StoreModel();
  userStore = new StoreModel();
  isLogedIn = false;
  clickedItem = 'profile';
  reload = true;

  constructor(
    public userService: UserService,
    public authService: AuthService,
    public dataServide: DataService,
    public authGuard: AuthGuard,
    private router: Router,
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService,
    private anyliticsService: AnyliticService,
    private promotionsService: PromotionsService
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
          console.log('el path', this.router.url);
          this.setCurrentNavigation();
          this.reload = false;
          setTimeout(() => {
            this.reload = true;
          }, 1);
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
    console.log('en el orchestrator por pedir el usuario');
    this.dataServide.getStoreInfo().then(store => {
      console.log(store);
      this.userStore = store;
      if (!this.userStore.profilePh) { this.userStore.profilePh = '/assets/noProfilePic.png'; }
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
    }).catch( error => {
      console.log('bueno revento todo, a crear la tienda? ', error);
      this.router.navigate(['Registrar-Tienda']);
    });
  }

  logout() {
    this.authService.doLogout().then(
      res => {
        this.dataServide.cleanCache();
        this.anyliticsService.cleanCache();
        this.promotionsService.cleanCache();
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

  setCurrentNavigation() {
    switch (true) {
      case (this.router.url === `/Tiendas/${this.userStore.storeName}`):
        this.clickedItem = 'profile';
      break;
      case (this.router.url.includes('Catalogo')):
        this.clickedItem = 'catalog';
      break;
      case (this.router.url.includes('Recomendaciones')):
        this.clickedItem = 'recomendations';
      break;
      case (this.router.url.includes('Estadisticas')):
          this.clickedItem = 'analytics';
      break;
      case (this.router.url.includes('Promociones')):
          this.clickedItem = 'promotions';
      break;
      case (this.router.url.includes('Configuracion')):
          this.clickedItem = 'config';
      break;

      default:
        break;
    }
  }

  ngOnDestroy(): void {}
  goToProfile() {
    this.clickedItem = 'profile';
    this.router.navigate(['Tiendas', this.userStore.storeName]);
  }

  goToInventory() {
    this.clickedItem = 'catalog';
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Catalogo`]);
  }

  goToRecomendations() {
    this.clickedItem = 'recomendations';
    this.router.navigate([
      `/Tiendas/${this.userStore.storeName}/Recomendaciones`
    ]);
  }

  goToAnalytics() {
    this.clickedItem = 'analytics';
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Estadisticas`]);
  }

  gotToConfiguration() {
    this.clickedItem = 'config';
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Configuracion`]);
  }

  goToAnalyticsPromotions() {
    this.clickedItem = 'promotions';
    this.router.navigate([`/Tiendas/${this.userStore.storeName}/Promociones`]);
  }
}
