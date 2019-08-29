import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../service/dataService';
import { StoreModel } from 'src/app/auth/models/store.model';
@Component({
  selector: 'app-configurations',
  templateUrl: './configurations.component.html',
  styleUrls: ['./configurations.component.scss']
})
export class ConfigurationsComponent implements OnInit {
  actualStore = new StoreModel();
  actualFirebaseUser;
  constructor( private dataService: DataService) {
    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
      console.log('la store en la configuracion', this.actualStore);
      this.actualFirebaseUser = this.dataService.getFirebaseUser();
      debugger;
    });
   }

  ngOnInit() {
  }


}
