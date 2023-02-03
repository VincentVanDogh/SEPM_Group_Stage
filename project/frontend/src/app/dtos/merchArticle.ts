export interface MerchArticle {
  id?: number;
  name: string;
  price: number;
  bonusPointPrice?: number;
  image: string;
  artistOrEventName: string;
}

export interface MerchArticleQuantity {
  articleNr: number;
  quantity: number;
  bonusUsed: boolean;
}

export interface MerchArticleSearch {
  term?: string;
  minPrice?: number;
  maxPrice?: number;
  minPointPrice?: number;
  maxPointPrice?: number;
  pointPurchaseAvailable: boolean;
  sortBy?: SortingType;
}

export enum SortingType {
  priceAsc = 'PRICE_ASC',
  priceDesc = 'PRICE_DESC',
  pointsAsc = 'POINTS_ASC',
  pointsDesc = 'POINTS_DESC'
}

export interface MerchPage {
  numberOfPages: number;
  merchArticles: MerchArticle[];
}
