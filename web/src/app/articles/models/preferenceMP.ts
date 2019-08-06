import { ProductMP } from './productMP';

export class PreferenceMP {
  items: Array<ProductMP>;
  payer: {
      name: string,
      surname?: string,
      email: string,
      phone?: {
          area_code?: string,
          number: number
      },
      identification: {
          readonly 'type': 'DNI', // Available ID types at https://api.mercadopago.com/v1/identification_types
          number: string
      },
      address?: {
          street_name: string,
          street_number: number,
          zip_code: string
      }
  };
  'back_urls'?: {
      'success': string,
      'failure': string
  };
  readonly auto_return: 'approved';
  payment_methods?: {
      readonly 'installments': 12,
      'default_payment_method_id': string,
      'default_installments': number
  };
  readonly 'notification_url'?: 'https://www.your-site.com/ipn';
  external_reference?: string;
  readonly 'expires'?: true;
  expiration_date_from?: string;
  expiration_date_to?: string;
  readonly 'binary_mode'?: true;
}

