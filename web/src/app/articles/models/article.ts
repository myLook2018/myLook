export class Article {
    articleId?: string;
    title?: string = '';
    code?: number = 0;
    picturesArray: string[] = [];
    cost?: number;
    sizes?: string[];
    material?: string;
    colors?: string[];
    initial_stock?: number;
    provider?: string;
    tags?: string[];
    storeName?: string;
    promotionLevel?: number;
  }
