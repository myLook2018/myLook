import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-pie-by-gender',
  templateUrl: './pie-by-gender.component.html',
  styleUrls: ['./pie-by-gender.component.css']
})
export class PieByGenderComponent implements OnInit {
  @Input() maleCounter = 0;
  @Input() femaleCounter = 0;
  @Input() otherGenderCounter = 0;

  isEmpty = false;
  data: any;
  options: { title: { display: boolean; text: string; fontSize: number; }; legend: { position: string; }; };

  constructor() {
    this.options = {
      title: {
          display: true,
          text: 'Utilización por género',
          fontSize: 24
      },
      legend: {
          position: 'bottom'
      }
  };
  }

  ngOnInit() {
    console.log('inicializando gender con ', this.maleCounter );
    console.log('inicializando gender con ', this.femaleCounter );
    console.log('inicializando gender con ', this.otherGenderCounter );

    if (!this.maleCounter && !this.femaleCounter && !this.otherGenderCounter) {this.isEmpty = true; }
    this.data = Object.assign({}, {
      labels: ['Masculino', 'Femenino', 'Otro'],
      datasets: [
          {
              data: [this.maleCounter, this.femaleCounter, this.otherGenderCounter],
              backgroundColor: [
                  '#36A2EB',
                  '#FF6384',
                  '#FFCE56'
              ],
              hoverBackgroundColor: [
                  '#36A2EB',
                  '#FF6384',
                  '#FFCE56'
              ]
          }]
      });
    }
}
