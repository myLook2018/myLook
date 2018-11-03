import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-donutchart',
  templateUrl: './donutchart.component.html',
  styleUrls: ['./donutchart.component.css']
})
export class DonutchartComponent implements OnInit {
  @Input() usersClickedArticle: any;
  @Input() articlesSavedToCloset: any;

  visited = 1;
  saved = 1;
  data: any;


  constructor() {
  }
  ngOnInit() {
    console.log(`dibujando ` + this.articlesSavedToCloset + this.usersClickedArticle);
    this.visited = this.usersClickedArticle;
    this.saved = this.articlesSavedToCloset;
    this.data = {
      labels: ['Articulos visitados', 'Articulos guardados en ropero'],
      datasets: [
        {
          data: [this.visited, this.saved],
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
