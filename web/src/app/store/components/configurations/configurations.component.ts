import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../service/dataService';
import { StoreModel } from 'src/app/auth/models/store.model';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-configurations',
  templateUrl: './configurations.component.html',
  styleUrls: ['./configurations.component.scss']
})
export class ConfigurationsComponent implements OnInit {
  actualStore = new StoreModel();
  actualFirebaseUser;
  myFormGroup: FormGroup;
  isEditMode = true;
  constructor( private dataService: DataService, private formBuilder: FormBuilder) {
   }

  ngOnInit() {
    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
      this.actualFirebaseUser = this.dataService.getFirebaseUser();
      console.log('+-'.repeat(15), this.actualStore);
      console.log('+-'.repeat(15), this.actualFirebaseUser);
      this.myFormGroup = this.formBuilder.group({
        storeAddress: [{value: this.actualStore.storeAddress, disabled: !this.isEditMode}, Validators.nullValidator],
        storeTower: [{value: this.actualStore.storeTower, disabled: !this.isEditMode}, Validators.nullValidator],
        storeFloor: [{value: this.actualStore.storeFloor, disabled: !this.isEditMode}, Validators.nullValidator],
        storeDept: [{value: this.actualStore.storeDept, disabled: !this.isEditMode}, Validators.nullValidator],
        storePhone: [{value: this.actualStore.storePhone, disabled: !this.isEditMode}, Validators.nullValidator],
        facebookLink: [{value: this.actualStore.facebookLink, disabled: !this.isEditMode}, Validators.nullValidator],
        instagramLink: [{value: this.actualStore.instagramLink, disabled: !this.isEditMode}, Validators.nullValidator],
        twitterLink: [{value: this.actualStore.twitterLink, disabled: !this.isEditMode}, Validators.nullValidator]
      });
    });
  }
}
