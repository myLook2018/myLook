import { Component, OnInit, Input } from '@angular/core';
declare let L;

@Component({
  selector: 'app-maps',
  templateUrl: './maps.component.html',
  styleUrls: ['./maps.component.scss']
})
export class MapsComponent implements OnInit {
  @Input() longitude;
  @Input() latitude;
  @Input() storeName: String;

  constructor() {
   }

  ngOnInit() {
    const myIcon = L.icon({
      iconUrl: '../assets/leaflet/images/marker-icon.png',
      shadowUrl: '../assets/leaflet/images/marker-shadow.png',

  });
    const map = L.map('map').setView([this.longitude, this.latitude], 13);
    const marker = L.marker([this.longitude, this.latitude], {icon: myIcon}).addTo(map);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', ).addTo(map);
    map.addLayer(marker);
  }
}


