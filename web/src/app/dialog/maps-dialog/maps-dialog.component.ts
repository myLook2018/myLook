import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

export interface GeoPosition {
  latitude: number;
  longitude: number;
}

export interface StoreData {
  storePosition: GeoPosition;
  storeName: string;
}

@Component({
  selector: 'app-maps-dialog',
  templateUrl: './maps-dialog.component.html',
  styleUrls: ['./maps-dialog.component.scss']
})
export class MapsDialogComponent {

  latitude: number;
  longitude: number;
  storeName: String;
  constructor(@Inject(MAT_DIALOG_DATA) storeData: any) {
    this.latitude = storeData.data.storePosition.longitude;
    this.longitude = storeData.data.storePosition.latitude;
    this.storeName = storeData.data.storeName;

  }


}
