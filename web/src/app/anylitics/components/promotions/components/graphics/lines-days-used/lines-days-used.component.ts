import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-lines-days-used',
  templateUrl: './lines-days-used.component.html',
  styleUrls: ['./lines-days-used.component.css']
})
export class LinesDaysUsedComponent implements OnInit {
  @Input() globalUsedByDay: any;
  @Input() campaingUsedByDay: any;
  @Input() daysOfTheWeek: any;

  data: any;
  options: any;


  constructor() {
    this.options = {
      title: {
          display: true,
          text: 'Utilización de cupones por dia',
          fontSize: 24
      },
      legend: {
          position: 'bottom'
      }
  };
  }
  ngOnInit() {

    this.data = Object.assign({}, {
      labels: this.daysOfTheWeek,
      datasets: [
          {
              label: 'Entre todas las campañas',
              // backgroundColor: '#42A5F5',
              borderColor: '#FFCE56',
              data: this.globalUsedByDay
          },
          {
              label: 'Campaña seleccionada',
              // backgroundColor: '#42A5F5',
              borderColor: '#ccf62e',
              data: this.campaingUsedByDay
          }
        ]
    });
  }
}
