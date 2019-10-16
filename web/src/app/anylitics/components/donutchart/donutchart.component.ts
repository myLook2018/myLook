import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-donutchart',
  templateUrl: './donutchart.component.html',
  styleUrls: ['./donutchart.component.css']
})
export class DonutchartComponent implements OnInit {
  @Input() negativeInteractions: any;
  @Input() positiveInteractions: any;

  liked = 0;
  disliked = 0;
  data: any;
  options;


  constructor() {
    this.options = {
      title: {
          display: true,
          text: 'Reacciones',
          fontSize: 24
      },
      legend: {
          position: 'bottom'
      }
  };
  }
  ngOnInit() {
    this.disliked = this.negativeInteractions;
    this.liked = this.positiveInteractions;
    this.data = {
      labels: ['Reacciones Positivas', 'Reacciones Negativas'],
      datasets: [
        {
          data: [this.liked, this.disliked],
          backgroundColor: [
            '#36A2EB',
            '#FF6384',
          ],
          hoverBackgroundColor: [
            '#36A2EB',
            '#FF6384',
          ]
        }]
    };
  }

}
