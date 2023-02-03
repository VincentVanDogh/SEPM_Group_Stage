import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {MerchArticle, MerchArticleQuantity} from '../dtos/merchArticle';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {MerchCartArticle, MerchPurchase} from '../dtos/merchPurchase';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MerchandiseService {

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) { }

  /**
   * Returns all merchandise articles
   */
  public getMerchandise(): Observable<MerchArticle[]> {
    return this.http.get<MerchArticle[]>(environment.merchArticleUrl);
  }

  /**
   * Returns all single merchandise article
   *
   * @param id the id of the merch article that should be returned
   */
  public getMerchArticle(id: number): Observable<MerchCartArticle> {
    if (this.authService.isLoggedIn()) {
      return this.http.get<MerchCartArticle>(`${environment.merchArticleUrl}/${id}`);
    } else {
      return this.http.get<MerchCartArticle>(`${environment.merchArticleUrl}/all/${id}`);
    }
  }

  /**
   * Adds a single merch article to the cart
   *
   * @param merchArticle article id and number of articles that should be added to the cart
   */
  public addMerchArticleToCart(merchArticle: MerchArticleQuantity): Observable<MerchPurchase> {
    return this.http.post<MerchPurchase>(environment.merchPurchaseUrl, merchArticle);
  }
}
