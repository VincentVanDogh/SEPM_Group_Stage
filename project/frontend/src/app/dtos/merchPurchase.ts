export interface MerchPurchase {
  id: number;
  purchased: boolean;
  articles: MerchCartArticle[];
}

export interface MerchCartArticle {
  articleNr: number;
  name: string;
  price: number;
  bonusPointPrice: number;
  image: string;
  articleCount: number;
  bonusUsed: boolean;
  artistOrEventName: string;
  allowedPointAmount?: number[];
}
