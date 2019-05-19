import { ProductMP } from './productMP';

export class PreferenceMP {
  items: Array<ProductMP>;
  payer: {
      name: string,
      surname?: string,
      email: string,
      phone?: {
          area_code?: string,
          number: string
      },
      identification: {
          readonly type: 'DNI', // Available ID types at https://api.mercadopago.com/v1/identification_types
          number: string
      },
      address: {
          street_name: string,
          street_number: number,
          zip_code: number
      }
  };
  back_urls: {
      readonly success: 'https://www.success.com',
      readonly failure: 'http://www.failure.com'
  };
  readonly auto_return: 'approved';
  payment_methods: {
      readonly installments: 12,
      default_payment_method_id: null,
      default_installments: null
  };
  readonly notification_url: 'https://www.your-site.com/ipn';
  external_reference?: string;
  readonly expires: true;
  expiration_date_from: Date;
  expiration_date_to: Date;
  readonly binary_mode: true;
}

