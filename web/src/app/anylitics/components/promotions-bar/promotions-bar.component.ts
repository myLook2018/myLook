import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-promotions-bar',
  templateUrl: './promotions-bar.component.html',
  styleUrls: ['./promotions-bar.component.css']
})
export class PromotionsBarComponent implements OnInit {
  @Input() level1Articles: any;
  @Input() level2Articles: any;
  @Input() level3Articles: any;
  data: any;
  options: any;


  constructor() {
    this.options = {
      title: {
        display: true,
        text: 'Interacciones por Promoción',
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
    const level1Real = (this.level1Articles / 3) + 1;
    const level2Real = (this.level2Articles + 10);
    const level3Real = (this.level3Articles + 8);
    console.log(`dibujando ` + level1Real + ` - ` + level2Real + ` - ` + level3Real);
    this.data = {
      labels: [`Niveles de promoción`],
      datasets: [
        {
          label: 'Articulos Sin promoción',
          backgroundColor: '#FF6384',
          borderColor: '#1E88E5',
          data: [this.level1Articles]
        },
        {
          label: 'Articulos con Promoción Básica',
          backgroundColor: '#42A5F5',
          borderColor: '#1E88E5',
          data: [this.level2Articles]
        },
        {
          label: 'Articulo con Promoción Premium',
          backgroundColor: '#FFCE56',
          borderColor: '#1E88E5',
          data: [this.level3Articles]
        }
      ]
    };
  }
}
