import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-donutchart',
  templateUrl: './donutchart.component.html',
  styleUrls: ['./donutchart.component.css']
})
export class DonutchartComponent implements OnInit {
  @Input() usersClickedArticle: any;
  @Input() articlesSavedToCloset: any;
  @Input() positiveInteractions: any;

  liked = 1;
  visited = 1;
  saved = 1;
  data: any;
  options;


  constructor() {
    this.options = {
      title: {
          display: true,
          text: 'Interacciones positivas',
          fontSize: 24
      },
      legend: {
          position: 'bottom'
      }
  };
  }
  ngOnInit() {
    console.log(`dibujando ` + this.articlesSavedToCloset + this.usersClickedArticle);
    this.visited = this.usersClickedArticle;
    this.saved = this.articlesSavedToCloset;
    this.liked = this.positiveInteractions;
    this.data = {
      labels: ['Articulos visitados', 'Articulos guardados en ropero', 'Articulos que han gustado'],
      datasets: [
        {
          data: [this.visited, this.saved, this.positiveInteractions],
          backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
          ],
          hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56'
          ]
        }]
    };
  }

}
