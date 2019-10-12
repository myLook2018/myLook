import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-views-by-promotions',
  templateUrl: './views-by-promotions.component.html',
  styleUrls: ['./views-by-promotions.component.css']
})
export class ViewsByPromotionsComponent implements OnInit {
  @Input() level1Articles: any;
  @Input() level2Articles: any;
  @Input() level3Articles: any;
  data: any;
  options: any;


  constructor() {
    this.options = {
      title: {
        display: true,
        text: 'Visitas según Promoción',
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
    const level1Real = (this.level1Articles);
    const level2Real = (this.level2Articles);
    const level3Real = (this.level3Articles);
    console.log(`dibujando visitas segun promocion: 0=` + level1Real + ` - 1=` + level2Real + ` - 2=` + level3Real);
    this.data = {
      labels: [],
      datasets: [
        {
          label: 'Visitas de artículos sin promoción',
          backgroundColor: '#FF6384',
          borderColor: '#1E88E5',
          data: [this.level1Articles]
        },
        {
          label: 'Visitas de artículos con promoción Básica',
          backgroundColor: '#42A5F5',
          borderColor: '#1E88E5',
          data: [this.level2Articles]
        },
        {
          label: 'Visitas de artículos con promoción Premium',
          backgroundColor: '#FFCE56',
          borderColor: '#1E88E5',
          data: [this.level3Articles]
        }
      ]
    };
  }
}
