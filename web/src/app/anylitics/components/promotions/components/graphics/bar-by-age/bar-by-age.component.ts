import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-bar-by-age',
  templateUrl: './bar-by-age.component.html',
  styleUrls: ['./bar-by-age.component.css']
})
export class BarByAgeComponent implements OnInit {
  @Input() lessThan18: any;
  @Input() from18to21: any;
  @Input() from22to25: any;
  @Input() from26to29: any;
  @Input() moreThan30: any;
  data: any;
  options: any;
  isEmpty: boolean;


  constructor() {
    this.options = {
      title: {
        display: true,
        text: 'Utilización por edad',
        fontSize: 24
      },
      legend: {
        position: 'bottom'
      },
      responsive: true,
      scales: {
        yAxes: [
          {
            ticks: {
              beginAtZero: true
            }
          }
        ]
      }
    };
  }

  ngOnInit() {
    if ( !this.lessThan18 && !this.from18to21 &&  !this.from22to25 && !this.from26to29 && !this.moreThan30 ) {
      this.isEmpty = true;
    }

    this.data = Object.assign({}, {
      labels: [],
      datasets: [
        {
          label: 'Menos de 18 años',
          backgroundColor: '#FF6384',
          borderColor: '#1E88E5',
          data: [this.lessThan18.toFixed(2)]
        },
        {
          label: 'Entre 18 y 21 años',
          backgroundColor: '#4BC0C0',
          borderColor: '#1E88E5',
          data: [this.from18to21.toFixed(2)]
        },
        {
          label:  'Entre 22 y 25 años',
          backgroundColor: '#FFCE56',
          borderColor: '#1E88E5',
          data: [this.from22to25.toFixed(2)]
        },
        {
          label:  'Entre 26 y 29 años',
          backgroundColor: '#E7E9ED',
          borderColor: '#1E88E5',
          data: [this.from26to29.toFixed(2)]
        },
        {
          label:  '30 años o más',
          backgroundColor: '#36A2EB',
          borderColor: '#1E88E5',
          data: [this.moreThan30.toFixed(2)]
        }
      ]
    });
  }
}
