import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-non-subs-success',
  templateUrl: './non-subs-success.component.html',
  styleUrls: ['./non-subs-success.component.css']
})
export class NonSubsSuccessComponent implements OnInit {
  @Input() used;
  @Input() nonUsed;
  data: any;
  options: any;
  isEmpty: boolean;


  constructor() {
    this.options = {
      title: {
        display: true,
        text: 'Utilización no Suscriptores',
        fontSize: 24
      },
      legend: {
        position: 'bottom'
      }
    };
  }

  ngOnInit() {
    if ( !this.used && !this.nonUsed) {
      this.isEmpty = true;
    }
    this.data = Object.assign({}, {
      labels: ['Cupones utilizados', 'Cupones no utilizados'],
      datasets: [
          {
              data: [this.used, this.nonUsed],
              backgroundColor: [
                  '#36A2EB',
                  '#FF6384'

                ],
              hoverBackgroundColor: [
                  '#36A2EB',
                  '#FF6384'              ]
          }]
      });
      console.log('lalalala', this.data);
    }

}
