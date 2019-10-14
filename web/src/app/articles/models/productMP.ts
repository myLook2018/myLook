export class ProductMP {
id?: string;
title: string;
currency_id: 'ARS';
picture_url?: string;
description?: string;
category_id?: string; // Available categories at https://api.mercadopago.com/item_categories
quantity: number;
unit_price: number;
}
