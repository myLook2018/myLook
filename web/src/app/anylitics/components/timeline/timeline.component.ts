import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-timeline',
  templateUrl: './timeline.component.html',
  styleUrls: ['./timeline.component.css']
})
export class TimelineComponent implements OnInit {
  @Input() interactionsByDay: any;
  @Input() daysOfTheWeek: any;

  countInteractionsByDay = [];
  days = [];
  data: any;
  options: any;


  constructor() {
    this.options = {
      title: {
          display: true,
          text: 'Cantidad de Interacciones por dia',
          fontSize: 24
      },
      legend: {
          position: 'bottom'
      }
  };
  }
  ngOnInit() {
    console.log(`dibujando ` + this.daysOfTheWeek + ` - ` + this.interactionsByDay);
    this.countInteractionsByDay = this.interactionsByDay;
    this.days = this.daysOfTheWeek;
    this.data = {
      labels: this.days,
      datasets: [
          {
              label: 'Número de interacciones en el día',
             // backgroundColor: '#42A5F5',
              borderColor: '#1E88E5',
              data: this.countInteractionsByDay
          }
      ]
  };
  /*
    this.data = {
      labels: this.tags,
      datasets: [
        {
          data: this.countOfTags,
          backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56',
            '#ccf62e',
            '#ebb329',
            '#7c7c7c',
            '#4c0000'
          ],
          hoverBackgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56',
            '#ccf62e',
            '#ebb329',
            '#7c7c7c',
            '#4c0000'
          ]
        }]
    };*/
  }

}
