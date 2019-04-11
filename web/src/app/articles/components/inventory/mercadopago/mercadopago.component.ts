import { Component, OnInit } from '@angular/core';
import * as Mercadopago from 'src/assets/lib/mercadopago.js';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormControl
} from '@angular/forms';

@Component({
  selector: 'app-mercadopago',
  templateUrl: './mercadopago.component.html',
  styleUrls: ['./mercadopago.component.css']
})
export class MercadopagoComponent implements OnInit {
  form: FormGroup;
  constructor(public fb: FormBuilder) {}

  ngOnInit() {
    console.log('+'.repeat(15));
    console.log('EL MERCADOPAGO', Mercadopago);
    console.log('+'.repeat(15));
    Mercadopago.setPublishableKey('TEST-98638d24-eb00-4dd5-82d8-4e573fac6a80');
    console.log('+'.repeat(15));
    console.log('EL MERCADOPAGO con credenciales', Mercadopago);
    console.log('tipos de dni', Mercadopago.getIdentificationTypes());

    this.form = this.fb.group({
      // completar los datos de la transaccion
    });
  }

  guessingPaymentMethod(event) {
    const bin = Mercadopago.getBin();

    if (event.type === 'keyup') {
      if (bin.length >= 6) {
        Mercadopago.getPaymentMethod(
          {
            bin: bin
          },
          Mercadopago.setPaymentMethodInfo
        );
      }
    } else {
      setTimeout(function() {
        if (bin.length >= 6) {
          Mercadopago.getPaymentMethod(
            {
              bin: bin
            },
            Mercadopago.setPaymentMethodInfo
          );
        }
      }, 100);
    }
  }
  getMedioPago(bin) {
    Mercadopago.getPaymentMethod(
      {
        bin: bin
      },
      Mercadopago.setPaymentMethodInfo
    );
  }

  setPaymentMethodInfo(status, response) {
    if (status === 200) {
      Mercadopago.paymentMethod.setAttribute('name', 'paymentMethodId');
      Mercadopago.paymentMethod.setAttribute('type', 'hidden');
      Mercadopago.paymentMethod.setAttribute('value', response[0].id);

      this.form.addControl(
        'paymentMethod',
        new FormControl(Mercadopago.paymentMethod, Validators.required)
      );
    } else {
      document.querySelector('input[name=paymentMethodId]').nodeValue =
        response[0].id;
    }
  }

  elMegaPayDefinitivo(){
    let doSubmit: any;
    doSubmit.addEvent(document.querySelector('#pay'), 'submit', doPay);
    function doPay(event){
        event.preventDefault();
        if(!doSubmit){
            var $form = document.querySelector('#pay');

            Mercadopago.createToken($form, sdkResponseHandler); // The function "sdkResponseHandler" is defined below

            return false;
        }
    };
  }
}
