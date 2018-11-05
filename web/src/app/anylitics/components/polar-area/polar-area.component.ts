import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-polar-area',
  templateUrl: './polar-area.component.html',
  styleUrls: ['./polar-area.component.css']
})
export class PolarAreaComponent implements OnInit {
  @Input() popularityOfTags: any;
  @Input() popularTags: any;

  countOfTags = [];
  tags = [];
  data: any;
  options: any;


  constructor() {
    this.options = {
      title: {
          display: true,
          text: 'Reacciones positivas según la etiqueta',
          fontSize: 24
      },
      legend: {
          position: 'bottom'
      }
  };
  }
  ngOnInit() {
    console.log(`dibujando ` + this.popularTags + ` - ` + this.popularityOfTags);
    this.countOfTags = this.popularityOfTags;
    this.tags = this.popularTags;
    this.data = {
      labels: this.tags,
      datasets: [
          {
              label: 'Reacciones positivas',
              backgroundColor: '#42A5F5',
              borderColor: '#1E88E5',
              data: this.countOfTags
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
