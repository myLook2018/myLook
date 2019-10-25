import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-polar-area',
  templateUrl: './polar-area.component.html',
  styleUrls: ['./polar-area.component.css']
})
export class PolarAreaComponent implements OnInit {
  @Input() tagsWithpopularity;

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
    this.tagsWithpopularity.forEach( tagXcount => {
      this.tags.push(tagXcount.tag);
      this.countOfTags.push(tagXcount.count);
    });
    this.data = {
      labels: this.tags,
      datasets: [
          {
              label: 'Reacciones positivas',
              backgroundColor: '#36A2EB',
              borderColor: '#1E88E5',
              data: this.countOfTags
          }
      ]
  };
  }

}
