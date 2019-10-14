import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-reactions-pie',
  templateUrl: './reactions-pie.component.html',
  styleUrls: ['./reactions-pie.component.css']
})
export class ReactionsPieComponent implements OnInit {
  @Input() likes;
  @Input() dislikes;
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
      }
    };
  }

  ngOnInit() {
    this.data = {
      labels: ['Reacciones Positivas en artículos promocionados', 'Reacciones Negativas en artículos promocionados'],
      datasets: [
          {
              data: [this.likes, this.dislikes],
              backgroundColor: [
                  "#36A2EB",
                  "#FF6384"

                ],
              hoverBackgroundColor: [
                  "#36A2EB",
                  "#FF6384"              ]
          }]
      };
  }
}
